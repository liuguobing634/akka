package lew.bing.akka.stream

import java.io.ByteArrayInputStream
import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{FileIO, StreamConverters}

/**
  * Created by 刘国兵 on 2017/4/14.
  */
object Demo5 {

  def main(args: Array[String]): Unit = {
    //无法转换
    implicit val system = ActorSystem("QuickStart")
    implicit val materializer = ActorMaterializer()
    import scala.concurrent.ExecutionContext.Implicits.global
    //测试is
    val source = StreamConverters.fromInputStream(() => new ByteArrayInputStream("I am better man!".getBytes))
    //测试os
    val sink = StreamConverters.fromOutputStream(() => System.out)
    val result = source.runWith(sink)
    val result2 =FileIO.fromPath(Paths.get("factorial2.txt")).to(FileIO.toPath(Paths.get("factorial3.txt"))).run()
    result.flatMap(f => result2).onComplete(_ => system.terminate())
  }

}
