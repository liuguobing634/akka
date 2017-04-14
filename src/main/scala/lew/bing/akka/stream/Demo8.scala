package lew.bing.akka.stream

import akka.actor.ActorSystem
import akka.stream.scaladsl.{GraphDSL, Sink, Source, ZipWith}
import akka.stream.{ActorMaterializer, SourceShape}

/**
  * Created by 刘国兵 on 2017/4/14.
  */
object Demo8 {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("QuickStart")
    implicit val materializer = ActorMaterializer()
    import scala.concurrent.ExecutionContext.Implicits.global

    val source = Source.fromGraph(GraphDSL.create(){implicit builder =>
      import GraphDSL.Implicits._
      val source1 = Source(1 to 100)
      val source2 = Source(1 to 100).map(_*2)

      val merge = builder.add(ZipWith((a1:Int,a2:Int) => {
        a1 + a2
      }))
      source1 ~> merge.in1
      source2 ~> merge.in0
      SourceShape(merge.out)
    })

    source.runWith(Sink.foreach(println)).onComplete(_ => System.exit(0))

  }
}
