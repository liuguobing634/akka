package lew.bing.akka.actor

import akka.actor.{Actor, ActorIdentity, ActorRef, ActorSystem, Identify, PoisonPill, Props, Terminated}
import akka.event.Logging
import akka.pattern.{ask, pipe}
import akka.stream.{ActorMaterializer, ThrottleMode}
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.util.Timeout

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by 刘国兵 on 2017/4/17.
  */
object Demo2 {

  class WatchActor extends Actor {

    val child: ActorRef = context.actorOf(Props.empty,"child")
    context.watch(child)
    val log = Logging(context.system,WatchActor.this)

    override def preStart(){
      log.info("preStart")
    }

    override def postStop(): Unit = {
      log.info("afterStop")
    }

    var lastSender: ActorRef = context.system.deadLetters

    def receive: PartialFunction[Any, Unit] = {
      case "kill" =>
        context.stop(child)
        lastSender = sender()
      case "hello" =>
        sender() ! "hello"
      case "finished" =>
        log.info("finished")
      case PoisonPill =>
        log.info("poison")
      case Terminated(`child` ) =>
        log.info("terminated")
        lastSender ! "finished"
    }
  }

  class Follower extends Actor {
    val identifyId = 1
    context.actorSelection("/user/another") ! Identify(identifyId)

    override def receive: Receive = {
      case ActorIdentity(`identifyId`,Some(ref)) =>
        context.watch(ref)
        context.become(active(ref))
      case ActorIdentity(`identifyId`,None) =>
        context.stop(self)
    }

    def active(another:ActorRef) :Actor.Receive = {
      case Terminated(`another`) => context.stop(self)
    }

  }

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("hello")
    implicit val materializer = ActorMaterializer()


    val watchActor = system.actorOf(Props[WatchActor],"watchActor")
    implicit val timeout = Timeout(5.seconds)
    watchActor ! "kill"
    val f = ask(watchActor,"hello")
    //3秒后关闭watch actor，并关闭系统
    Source.single(1).delay(3.second).runForeach(_ => watchActor ! PoisonPill).onComplete(_ => System.exit(0))
  }

}
