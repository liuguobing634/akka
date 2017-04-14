package lew.bing.akka.stream

import akka.actor.ActorSystem
import akka.stream.scaladsl._
import akka.stream.{ActorMaterializer, FlowShape}


/**
  * Created by 刘国兵 on 2017/4/13.
  */
object Demo4 {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("QuickStart")
    implicit val materializer = ActorMaterializer()
    import scala.concurrent.ExecutionContext.Implicits.global

    val flow = Flow.fromGraph(GraphDSL.create(){implicit b =>
      import GraphDSL.Implicits._
      val broadcast = b.add(Broadcast[Int](2))
      val zip = b.add(Zip[Int,String]())
      broadcast.out(0).map(identity).map(_+1) ~> zip.in0
      broadcast.out(1).map(_.toString) ~> zip.in1


      FlowShape(broadcast.in,zip.out)
    })
    val source = Source.single(2)
    flow.runWith(source.take(4),Sink.seq)._2.onComplete(println)
    Thread.sleep(1000)
    System.exit(0)
  }
}
