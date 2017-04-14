package lew.bing.akka.stream

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Merge, RunnableGraph, Sink, Source}
import akka.stream.{ActorMaterializer, ClosedShape}

/**
  * Created by 刘国兵 on 2017/4/13.
  */
object Demo3 {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("QuickStart")
    implicit val materializer = ActorMaterializer()
    val g = RunnableGraph.fromGraph(GraphDSL.create(){implicit builder:GraphDSL.Builder[NotUsed] =>
      import GraphDSL.Implicits._
      val in = Source(1 to 10)
      val out = Sink.foreach(println)

      val bcast = builder.add(Broadcast[Int](2))
      val merge = builder.add(Merge[Int](2))

      val f1, f2, f3, f4 = (k:Int) => Flow[Int].map(i => {
        println(s"操作$k : $i")
        i + 10
      })
      in ~> f1(1) ~> bcast ~> f2(2) ~> merge ~> f3(3) ~> out

      bcast ~> f4(4) ~> merge
      ClosedShape
    })

    g.run()
    Thread.sleep(3000)
    System.exit(0)
  }

}
