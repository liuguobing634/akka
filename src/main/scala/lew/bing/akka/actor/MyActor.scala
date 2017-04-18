package lew.bing.akka.actor

import akka.actor.{Actor, ActorSystem, Inbox, Props}
import akka.event.Logging

/**
  * Created by 刘国兵 on 2017/4/15.
  */
class MyActor extends Actor{

  val log = Logging(context.system,this)

  val otherActor = context.actorOf(ActorWithArgs.props("other"),name = "otherActor")

  override def receive: Receive = {
    case "test" => log.info("收到test")
    case a       => otherActor !  a
  }

}

class ActorWithArgs(args:String*) extends Actor{
  val log = Logging(context.system,this)
  val arg: String = if (args.nonEmpty) args(0) else "hello"
  
  override def receive: Receive = {
    case `arg` => log.info("猜对了")
    case _    => log.info("猜错了")
  }
}

object ActorWithArgs {
  def props(args:String*):Props = Props(new ActorWithArgs(args:_*))
}

case class MyValueClass(v:Int) extends AnyVal



object MyActor {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("hello")
    val props1 = Props[MyActor]
    val props2 = ActorWithArgs.props("arg")//不建议这么使用，因为
    val actor1 = system.actorOf(props1,"actor1")
    val actor2 = system.actorOf(props2,"actor2")
    actor1 ! "test"
    actor1 ! "other"
    actor2 ! "arg"
    actor2 ! "hello"

  }
}
