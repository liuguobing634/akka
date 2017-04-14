package lew.bing.akka.stream


import akka.actor.{ActorSystem, Cancellable}
import akka.stream.scaladsl._
import akka.stream.{ActorMaterializer, ThrottleMode}

import scala.concurrent.duration._
import scala.concurrent.{Future, Promise}

/**
  * Created by 刘国兵 on 2017/4/13.
  */
object Demo2 {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("QuickStart")
    implicit val materializer = ActorMaterializer()
    val source:Source[Int,Promise[Option[Int]]] = Source.maybe[Int]
    //怎么创建
//    val flow: Flow[Int, Int, Cancellable] = Throttler.RateInt(1)
    val cancellable = new Cancellable {
      private var cancelled = false

      override def cancel(): Boolean = {
        cancelled = true
        system.terminate()
        isCancelled
      }

      override def isCancelled: Boolean = cancelled
    }
    val flow:Flow[Int, Int, Cancellable] = Flow.fromProcessorMat(() => {
      (null, cancellable)
    }).map(o => 1).throttle(1, 1.second, 1, ThrottleMode.Shaping)

    val sink:Sink[Int,Future[Int]] = Sink.head[Int]
    val r1:RunnableGraph[Promise[Option[Int]]] = source.via(flow).to(sink)
    val r2:RunnableGraph[Cancellable] = source.viaMat(flow)(Keep.right).to(sink)
    val r3 = source.via(flow).toMat(sink)(Keep.right)

    val r4 = source.via(flow).runWith(sink)
    val r5 = flow.to(sink).runWith(source)
    val r6 = flow.runWith(source,sink)



  }

}
