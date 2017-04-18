package lew.bing.akka.actor

import akka.actor.{Actor, ActorRef, ActorSystem, PoisonPill, Props, Terminated}
import akka.event.Logging

/**
  * Created by 刘国兵 on 2017/4/17.
  */
object Demo3 {

  class Cruncher extends Actor {

    val log = Logging(context.system,this)

    override def receive: Receive = {
      case "crunch" =>
        log.info(s"working...")
      case Terminated(`self`) =>
        log.info("offline")
    }

    override def postStop(): Unit = {
      log.info("offline")
    }
  }


  object Manager {
    case object Shutdown
  }

  class Manager extends Actor{

    val log = Logging(context.system,this)

    import Manager._
    val worker: ActorRef = context.watch(context.actorOf(Props[Cruncher],name = "worker"))

    override def receive: Receive = {
      case "job" => worker ! "crunch"
      case Shutdown =>
        worker ! PoisonPill
        context become shuttingDowm
    }

    def shuttingDowm: Receive = {
      case "job" => sender() ! "服务不可到达"
      case Terminated(`worker`) =>
        log.info("shut down")
        context stop self
    }
  }

  def main(args: Array[String]): Unit = {
    val system = ActorSystem("hello")
    val manager = system.actorOf(Props[Manager],"manager")
    manager ! "job"
    manager ! Manager.Shutdown
  }


}
