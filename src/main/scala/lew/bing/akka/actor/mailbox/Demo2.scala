package lew.bing.akka.actor.mailbox

import akka.actor.{Actor, ActorSystem, PoisonPill, Props, Terminated}
import akka.dispatch.ControlMessage
import akka.event.{Logging, LoggingAdapter}

/**
  * Created by 刘国兵 on 2017/4/27.
  */
object Demo2 {

  case object MyControlMessage extends ControlMessage

  class Logger extends Actor {

    val log:LoggingAdapter = Logging(context.system,this)

    self ! 'foo
    self ! 'bar
    self ! MyControlMessage
    self ! PoisonPill

    override def receive: Receive = {
      case x => log.info(x.toString)
    }

    override def postStop(): Unit = {
      context.system.terminate()
    }
  }

  def main(args: Array[String]): Unit = {
    val system = ActorSystem("mailbox-demo2")
    val a      = system.actorOf(Props[Logger].withDispatcher("control-aware-dispatcher"))

  }

}
