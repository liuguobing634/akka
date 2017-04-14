package lew.bing.akka.stream

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}

/**
  * Created by 刘国兵 on 2017/4/14.
  */
object Demo6 {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("QuickStart")
    implicit val materializer = ActorMaterializer()
    import scala.concurrent.ExecutionContext.Implicits.global
    val source = Source.unfold(0 -> 1){
      case (a, _) if a > 10000000 => None
      case (a,b) => Some((b -> (a + b)) -> a)
    }
    source.runWith(Sink.foreach(s => println(s))).onComplete(_ => System.exit(0))
  }
}
