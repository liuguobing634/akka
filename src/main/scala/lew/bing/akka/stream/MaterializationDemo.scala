package lew.bing.akka.stream

import akka.NotUsed
import akka.actor.{ActorRef, ActorSystem}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.stream.scaladsl.Source

import scala.concurrent.ExecutionContext
import scala.io.StdIn

/**
  * Created by 刘国兵 on 2017/8/5.
  */
object MaterializationDemo {

  var _ref:Option[ActorRef] = Option.empty

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("QuickStart")
    implicit val materializer = ActorMaterializer()
    implicit val ec:ExecutionContext = system.dispatcher
    val source = Source.actorRef[String](1,OverflowStrategy.dropNew).mapMaterializedValue(ref => {
      abc(ref)
    })
    source.runForeach(println)

    _ref.foreach(ref => {
      ref ! "hello"

      ref ! "abc"
    })
    StdIn.readLine()
    _ref.foreach(ref => {
      ref ! "hello"

      ref ! "abc"
    })
    StdIn.readLine()
    system.terminate()

  }

  def abc(red: ActorRef): NotUsed = {
    println(red)
    _ref = Some(red)
    NotUsed
  }

}
