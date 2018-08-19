package lew.bing.akka.actor

import akka.actor.{Actor, ActorSystem, Props}
import akka.event.Logging

import scala.io.StdIn


/**
  * Created by 刘国兵 on 2017/4/17.
  */
object Demo4 {

  case object Swap

  class Swapper extends Actor {

    import context._

    val log = Logging(system,this)

    override def receive: Receive = {
      case Swap =>
        log.info("Hi")
        become({
          case Swap =>
            log.info("Ho")
            unbecome()
        },discardOld = false)
    }
  }

  def main(args: Array[String]): Unit = {
    val system = ActorSystem("SwapperSystem")
    val swap = system.actorOf(Props[Swapper],name = "swapper")
    swap !Swap
    swap !Swap
    swap !Swap
    swap !Swap
    swap !Swap
    swap !Swap
    swap !Swap
    swap !Swap
    StdIn.readLine()
    system.terminate()

  }

}
