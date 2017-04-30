package lew.bing.akka.actor.mailbox

import akka.actor.{Actor, ActorSystem, PoisonPill, Props}
import akka.event.{Logging, LoggingAdapter}

/**
  * Created by 刘国兵 on 2017/4/27.
  */
object Demo1 {

  def main(args: Array[String]): Unit = {
    val system = ActorSystem("mailbox-demo1")
    val a = system.actorOf(Props(classOf[Logger]).withDispatcher("prio-dispatcher"))
    Thread.sleep(2000)
    system.terminate()
  }

  class Logger extends Actor {

    val log:LoggingAdapter = Logging(context.system,this)

    self ! 'lowpriority
    self ! 'lowpriority
    self ! 'highpriority
    self ! 'pigdog
    self ! 'pigdog2
    self ! 'pigdog3
    self ! 'highpriority
    self ! PoisonPill

    override def receive: Receive = {
      case x => log.info(x.toString)
    }
  }

}
