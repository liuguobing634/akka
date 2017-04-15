package lew.bing.akka.stream

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, SourceShape}
import akka.stream.scaladsl._
import scala.concurrent.duration._
import scala.concurrent.Await

/**
  * Created by 刘国兵 on 2017/4/14.
  */
object Demo9 {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("QuickStart")
    implicit val materializer = ActorMaterializer()
    import scala.concurrent.ExecutionContext.Implicits.global

    val pairs = Source.fromGraph(GraphDSL.create(){implicit b =>
      import GraphDSL.Implicits._

      val zip = b.add(Zip[Int,Int]())
      def ints = Source.fromIterator(() => Iterator.from(1))

      ints.filter(_ % 2 !=  0) ~> zip.in0
      ints.filter(_ % 2 ==  0) ~> zip.in1
      SourceShape(zip.out)
    })
    val runWith = pairs.take(20).runWith(Sink.foreach(println))
    runWith.onComplete(_ =>System.exit(0))


  }

}
