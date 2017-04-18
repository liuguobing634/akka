package lew.bing.scala

import akka.stream.scaladsl.{Broadcast, Source}

import scala.concurrent.Promise
import scala.util.Random

/**
  * Created by 刘国兵 on 2017/4/17.
  */
object QuickSort {


  def sort(seq: Seq[Int]):Seq[Int] = {
    //如果没有元素则返回
    if (seq.isEmpty) seq else {
      val head = seq.head
      val (left,right) = seq.tail.foldLeft((Seq.empty[Int],Seq.empty[Int])){(a,b) =>
        if (b < head) (a._1 :+ b,a._2) else (a._1,a._2 :+ b)
      }
      (sort(left) :+ head) ++ sort(right)
    }

  }

  def main(args: Array[String]): Unit = {
    val random = Random
    val seq = (0 to 100).map(_ => random.nextInt(100))
    println(seq.sorted)
    println(sort(seq))

  }

}
