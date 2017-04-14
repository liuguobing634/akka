package lew.bing.akka.stream

import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._
import akka.util.ByteString

import scala.concurrent._
import scala.concurrent.duration._

object Hello {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("QuickStart")
    implicit val materializer = ActorMaterializer()
    val source = Source(1 to 100)
    source.runForeach(i => println(i))
    val factorials = source.scan(BigInt(1))((acc,next) => acc*next)
    val result = factorials
      .map(num => ByteString(s"$num\n"))
      .runWith(FileIO.toPath(Paths.get("factorials.txt")))
    //reusable
    def lineSink(filename: String): Sink[String, Future[IOResult]] =
      Flow[String]
        .map(s => ByteString(s + "\n"))
        .toMat(FileIO.toPath(Paths.get(filename)))(Keep.right)
    factorials.map(_.toString).runWith(lineSink("factorial2.txt"))
    factorials.zipWith(Source(0 to 100))((num, idx) => s"$idx! = $num").throttle(1, 1.second,1,ThrottleMode.Shaping).runForeach(println)
    Thread.sleep(110000)
    System.exit(0)
  }




}

