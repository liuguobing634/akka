package lew.bing.akka.actor

import akka.actor.SupervisorStrategy._
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, OneForOneStrategy, Props, ReceiveTimeout, SupervisorStrategy, Terminated}
import akka.event.LoggingReceive
import akka.util.Timeout
import akka.pattern.{ask, pipe}
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._

/**
  * Created by 刘国兵 on 2017/4/18.
  * Fault Tolerance Sample
  */
object Demo7 {

  def main(args: Array[String]): Unit = {
    import Worker._

    val config = ConfigFactory.parseString(
      """
         akka.loglevel = "DEBUG"
         akka.actor.debug {
          receive = on
          lifecycle = on
         }
      """.stripMargin)

    val system = ActorSystem("FaultToleranceSample",config)
    //顶级的actor只有两个，一个是worker，一个是listener
    val worker = system.actorOf(Props[Worker], name="worker")
    val listener = system.actorOf(Props[Listener], name="listener")
    // 开始工作然后监听进程
    worker.tell(Start,sender = listener)
  }

  /*
  *  监听worker的进程以及当足够多的work完成之后关闭系统
  * */
  class Listener extends Actor with ActorLogging {
    import Worker._
    //如果15秒内没有得到任何进度，服务就不可用
    context.setReceiveTimeout(15.seconds)

    override def receive: Receive = {
      case Progress(percent) =>
        log.info("Current progress: {} %",percent)
        if (percent >= 100.0) {
          log.info("That's all, shutting down")
          context.system.terminate()
        }
      case ReceiveTimeout =>
        //15秒没有进度，服务就不可用，系统关闭
        log.error("Shutting down due to unavailable service")
        context.system.terminate()
    }
  }

  object Worker {
    case object Start
    case object Do
    final case class Progress(percent: Double)
  }
  /**
   *   Worker 进行一些工作当他收到 `Starr` 信息
   *   他将持续的通知 `Start` 信息的发送者当前进程
    *  `Worker` 监督 `CounterService`
    *  */
  class Worker extends Actor with ActorLogging {
    import Worker._
    import CounterService._
    implicit val askTimeout = Timeout(5.seconds)

    //如果抛出ServiceUnavailable，关闭CounterService
    override val supervisorStrategy = OneForOneStrategy(){
      case _: CounterService.ServiceUnavailable => {
        log.info("有异常，关闭counterService???")
        Stop
      }
    }

    var progressListener:Option[ActorRef] = None
    //counterService是worker的子actor，如果有ServiceUnavailable，就停掉它
    val counterService = context.actorOf(Props[CounterService],name = "counter")
    val totalCount = 51
    import context.dispatcher

    override def receive: Receive = {
      case Start if progressListener.isEmpty =>
        //收到start切progressListener空时初始化progressListener为sender
        progressListener = Some(sender())
        //每隔一秒钟给自己发送一个Do
        context.system.scheduler.schedule(Duration.Zero,1.second,self,Do)
      case Do =>
        //增加三次
        counterService ! Increment(1)
        counterService ! Increment(1)
        counterService ! Increment(1)
        //获得工作进度，如果成功发送给empty时那个sender当前进度，否则发送一个Failure信号
        counterService ? GetCurrentCount map {
          case CurrentCount(_,count) => Progress(100.0 * count / totalCount)
        } pipeTo progressListener.get
    }
  }

  object CounterService {
    final case class Increment(n:Int)
    sealed abstract class GetCurrentCount
    case object GetCurrentCount extends GetCurrentCount
    final case class CurrentCount(key:String,count:Long)
    class ServiceUnavailable(msg: String) extends RuntimeException(msg)

    private case object Reconnect
  }

  /**
    *  当收到 `Increment` 信息时，将它的值添加到一个持久化的counter
    *  回复 `CurrentCount` 当它被要求发送 `CounterCount`
    *  `CounterService` 监督 `Storage` 和 `Counter`
    *  * */
  class CounterService extends Actor {
    import CounterService._
    import Counter._
    import Storage._


    //重启storage当抛出StorageException
    //三次重启五秒后就关闭
    override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy(
      maxNrOfRetries = 3,
      withinTimeRange = 5.seconds
    ) {
      case _: Storage.StorageException => {
        println("有异常，重启？")
        Restart
      }
    }

    val key: String = self.path.name
    var storage: Option[ActorRef] = None
    var counter: Option[ActorRef] = None
    var backlog: IndexedSeq[(ActorRef, Any)] = IndexedSeq.empty[(ActorRef, Any)]
    val MaxBacklog = 10000

    import context.dispatcher


    override def preStart(): Unit = {
      //启动前初始化storage
      initStorage()
    }


    def initStorage(): Unit ={
      storage = Some(context.watch(context.actorOf(Props[Storage],name = "storage")))
      //将counter发送给新的storage
      counter foreach {_ ! UseStorage(storage)}

      storage.get ! Get(key)
    }

    override def receive: Receive = LoggingReceive {
      case Entry(k,v) if k == key && counter.isEmpty =>
        //这个信息storage发送的，第一次init时候
        val c = context.actorOf(Props(classOf[Counter],key,v))
        counter = Some(c)

        c ! UseStorage(storage)

        for ((replyTo, msg) <- backlog) c.tell(msg, sender = replyTo)
        backlog = IndexedSeq.empty
      case msg: Increment => forwardOrPlaceInBacking(msg)
      case msg:GetCurrentCount => forwardOrPlaceInBacking(msg)
      case Terminated(actorRef) if Some(actorRef) == storage =>
        storage = None
        counter foreach {_ ! UseStorage(None)}
        //10秒后发送重启命令
        context.system.scheduler.scheduleOnce(10.seconds,self,Reconnect)
      case Reconnect =>
        //重连命令后初始化storage
        initStorage()
    }

    def forwardOrPlaceInBacking(msg: Any): Unit = {
      counter match  {
          //如果counter存在，就发送消息
        case Some(c) => c forward msg
        case None =>
          //没有的话就在backlog中添加该sender和msg
          if (backlog.size > MaxBacklog)
            throw new ServiceUnavailable("CounterService not available, lack of initial value")
          backlog :+= (sender() -> msg)
      }
    }

    override def postStop(): Unit = {
      println("stop")
    }
  }

  object Counter {
    final case class UseStorage(storage:Option[ActorRef])
  }

  /**
  *  如果当前有可用的storage的话，就把内存中的count变量发送到 `Storage`
  * */
  class Counter(key: String,initialValue:Long) extends Actor{
    import Counter._
    import CounterService._
    import Storage._

    var count = initialValue
    var storage: Option[ActorRef] = None


    override def receive: Receive = LoggingReceive{
      case UseStorage(s) =>
        storage = s
        storeCount()
      case Increment(n) =>
        //自身也维护这个变量，当storage从不可用到可用时，这个量保持正确的
        count += n
        storeCount()
      case GetCurrentCount =>
        sender() ! CurrentCount(key,count)
    }

    def storeCount(): Unit = {
      //委托危险的工作，保护变量状态
      //在没有storage时仍然能够工作
      storage.foreach {_ ! Store(Entry(key,count))}
    }
  }

  object Storage {
    final case class Store(entry:Entry)
    final case class Get(key:String)
    final case class Entry(key:String,value: Long)
    class StorageException(msg:String) extends RuntimeException(msg)
  }

  /**
    * 当收到 `Store` 信息时，保存键值对到当前持久化的storage
    * 当收到 `Get` 信息时，回复当前的值
    * 如果 数据存储器超过了范围就跑出StorageException
    * */
  class Storage extends Actor{
    import Storage._
    val db = DummyDB

    override def receive: Receive = LoggingReceive {
      case Store(Entry(key,value)) => db.save(key,value)
      case Get(key)                =>
        sender() ! Entry(key,db.load(key).getOrElse(0L))
    }

    override def postRestart(reason: Throwable): Unit = {
      println(reason)
    }
  }

  object DummyDB {
    import Storage.StorageException
    private var db = Map[String,Long]()

    @throws(classOf[StorageException])
    def save(key:String,value:Long):Unit= synchronized{
      if (11 <= value && value <= 14)
        throw new StorageException("Simulated store failure " + value)
      db += (key -> value)
    }

    @throws(classOf[StorageException])
    def load(key:String):Option[Long] = synchronized{
      db.get(key)
    }
  }

}
