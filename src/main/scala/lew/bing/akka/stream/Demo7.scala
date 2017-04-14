package lew.bing.akka.stream

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}

/**
  * Created by 刘国兵 on 2017/4/14.
  */
object Demo7 {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("QuickStart")
    implicit val materializer = ActorMaterializer()
    import scala.concurrent.ExecutionContext.Implicits.global
    val source = Source.maybe[Int]
    val s = source.to(Sink.foreach(println))
    val run = s.run()
    run.trySuccess(Some(3))
    run.trySuccess(Some(2))
    run.trySuccess(Some(2))
    run.future.onComplete(t => {
      println(t)
      System.exit(0)
    })

  }
}
