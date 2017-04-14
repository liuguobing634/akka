package lew.bing.akka.io

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source, Tcp}
import akka.util.ByteString

/**
  * Created by 刘国兵 on 2017/4/14.
  */
object TcpDemo {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("QuickStart")
    implicit val materializer = ActorMaterializer()
    import scala.concurrent.ExecutionContext.Implicits.global
    val tcp =Tcp()
    val sink = Sink.foreach((t:ByteString) => {
      println(t)
    })
    val source = Source.single(ByteString("helloa"))
    try {
      tcp.bindAndHandle(Flow.fromSinkAndSource(sink,source),"127.0.0.1",8877,halfClose = true).onComplete(println)
    }catch {
      case e:Exception =>
        e.printStackTrace()
        System.exit(0)

    }
  }

}
