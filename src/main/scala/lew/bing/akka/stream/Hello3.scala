package lew.bing.akka.stream

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Keep, Sink, Source}

import scala.concurrent.Future


/**
  * Created by 刘国兵 on 2017/4/13.
  */
object Hello3 {

  def main(args:Array[String]): Unit ={
    implicit val system = ActorSystem("QuickStart")
    implicit val materializer = ActorMaterializer()
    import scala.concurrent.ExecutionContext.Implicits.global
    val source = Source(1 to 10)
    val sink = Sink.foldAsync[Int,Int](0)((a,b) => {
      Future.successful(a+b)
    })
    val runnable = source.toMat(sink)(Keep.right)
    val sum1 = runnable.run()
    val sum2 = runnable.run()
    sum1.onComplete(r => if(r.isSuccess){
      println(s"结果一：${r.get}")
    }else {
      println("结果一错误")
    })
    sum2.onComplete(r => if(r.isSuccess){
      println(s"结果二：${r.get}")
    }else {
      println("结果二错误")
    })
    while (!sum1.isCompleted || !sum2.isCompleted) {
      Thread.sleep(100)
    }
    System.exit(0)
  }


}
