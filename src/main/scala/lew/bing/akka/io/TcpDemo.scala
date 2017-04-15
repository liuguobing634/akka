package lew.bing.akka.io

import java.net.InetSocketAddress
import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Tcp.{IncomingConnection, ServerBinding}
import akka.stream.scaladsl.{Flow, GraphDSL, Sink, Source, Tcp}
import akka.util.ByteString

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

/**
  * Created by 刘国兵 on 2017/4/14.
  */
object TcpDemo {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("QuickStart")
    implicit val materializer = ActorMaterializer()
    import scala.concurrent.ExecutionContext.Implicits.global
//    val tcp =Tcp()
    val address = "localhost"
    val port = 8877
//    val binding = tcp.bind(interface = address,port = port).to(Sink.ignore).run()
//    binding.map(b => {
//      b.unbind()
//    })
    import akka.stream.scaladsl.Framing

    val connections: Source[IncomingConnection, Future[ServerBinding]] =
      Tcp().bind(address, port)
    connections runForeach { connection =>
      val commandParser = Flow[String].takeWhile(_ != "BYE",inclusive = true).map(_ + "!")

      import connection._
      val welcomeMsg = s"Welcome to: $localAddress, you are: $remoteAddress!"
      val welcome = Source.single(welcomeMsg)

      val serverLogic = Flow[ByteString]
        .via(Framing.delimiter(
          ByteString("\r\n"),
          maximumFrameLength = 256,
          allowTruncation = true))
        .map(_.utf8String)
        .via(commandParser)
        // merge in the initial banner after parser
        .merge(welcome)
        .map(_ + "\r\n")
        .map(ByteString(_))

      connection.handleWith(serverLogic)
    }




//    source.via()
  }

}
