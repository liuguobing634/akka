package lew.bing.scala

import scala.util.Random

/**
  * Created by 刘国兵 on 2017/4/17.
  */
object HeapSort {


  def sort(A:Seq[Int]):Seq[Int] = {
    if(A.isEmpty || A.length == 1) A else {
      def buildMaxHeap(A:Seq[Int]):Seq[Int] = {
        (0 until A.length/2).map(A.length/2-1-_).foldLeft(A)((o,i) =>maxHeap(o,i))
      }

      def maxHeap(A:Seq[Int],root:Int):Seq[Int] = {
        val left = root * 2 +1
        val right = root * 2 + 2
        //判断seq（root)\seq(left)\seq(right)哪个大
        var large =  if(left<A.length && A(left) > A(root)) left else root
        large = if (right < A.length && A(right) > A(large)) right else large
        if(large == root) A else maxHeap((A.slice(0,root) :+ A(large)) ++: (A.slice(root+1,large) :+ A(root)) ++: A.takeRight(A.length-large-1), large)
      }

      def switch(S:Seq[Int]):Seq[Int] = {
        if (S.length < 2) S else {
          switch(maxHeap(S.last +: S.slice(1,S.length-1),0)) :+ S.head
        }
      }
      //首先构造大根堆
      switch(buildMaxHeap(A))
    }
  }

  def main(args: Array[String]): Unit = {
    val random = Random
    val seq = (0 to 100).map(_ => random.nextInt(100))
    println(seq.sorted)
    println(sort(seq))
  }

}
