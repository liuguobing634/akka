package lew.bing.akka.actor

import akka.actor.Props
import akka.cluster.ddata.VersionVector.Same
import akka.typed._
import akka.typed.scaladsl.Actor._
import akka.typed.scaladsl.AskPattern._
import akka.util.Timeout
import akka.pattern._
import akka.typed.scaladsl.Actor

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.concurrent.Await
/**
  * Created by 刘国兵 on 2017/4/18.
  *
  * actor 模型是1973年Hewitt, Bishop and Steiger提出的一种计算模型
  */
object Demo5 {

  object HelloWorld {
    final case class Greet(whom:String,replyTo:ActorRef[Greeted])
    final case class Greeted(whom:String)
    val greeter = Actor.immutable[Greet] { (_,msg) =>
      println(s"Hello ${msg.whom}!")
      msg.replyTo ! Greeted(msg.whom)
      Actor.same
    }
  }

  def main(args: Array[String]): Unit = {
    import HelloWorld._
    import scala.concurrent.ExecutionContext.Implicits.global
    implicit val timeout = Timeout(5.second)

    //这里与官方教程有些不一样
    val system:ActorSystem[Greet] = ActorSystem("hello", greeter)
    implicit val scheduler = system.scheduler
    val future:Future[Greeted] = system ? (Greet("hello",_))
    for {
      greeting <- future.recover {case ex => ex.getMessage}
      done <- {println(s"result:$greeting");system.terminate()}
    }println("system terminated")

  }


}
