package lew.bing.akka.stream

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.{ActorMaterializer}

/**
  * Created by 刘国兵 on 2017/4/13.
  */
object Demo1 {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("QuickStart")
    implicit val materializer = ActorMaterializer()
    val flow = Flow[Int].map(_ * 2).filter(_ > 500)

    val source = Source.fromIterator {() => Iterator from 0}.via(flow).take(1000)
//    source.runForeach(i => println(i))
    val runnable =Source(1 to 100).map(i => {
      println(s"第一个：${Thread.currentThread().getName} - $i")
      i + 1
    }).async.map(i => {
      //第一个第二个偶尔交叉,如果没有async，那第一个第二个绝对是交叉,而且是同一个线程
      println(s"第二个：${Thread.currentThread().getName} - $i")
      i * 2
    }).to(Sink.ignore).run()
  }

}
