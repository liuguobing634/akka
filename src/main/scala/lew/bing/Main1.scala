package lew.bing
import org.slf4j.{Logger, LoggerFactory}

import scala.reflect.runtime.universe._


/**
  * Created by 刘国兵 on 2017/5/8.
  */
object Main1 {

  def main(args: Array[String]): Unit = {
//    val  factorialSeq:Stream[Long] = {
//      def loop(h:Long,n:Long):Stream[Long] = h #:: loop(h*n,n+1)
//      loop(1,1)
//    }
//    val expSeq = factorialSeq.map(1.0 / _)
//    println(expSeq.take(30).sum)
//    println(pow(2.0,0.5)*pow(1.5,0.5))

    val log = LoggerFactory.getLogger(Main1.getClass)
    log.error("sss")

  }



  // 求x的n次方
  def pow(x:Double,n:Double): Double = {
    val times:Stream[Double] = {
      //修改下
      def loop(a:Double,k:Long):Stream[Double] = a #:: loop(a * (n-k+1) / k,k+1)
      loop(1.0,1)
    }
    val xs:Stream[Double] = {
      def loop(next:Double):Stream[Double] = next #:: loop(next * (x-1))
      loop(1)
    }
    times.zip(xs).take(120).map(k =>{
      println(k)
      k._1 * k._2
    }).sum
  }

  def gca(a:Long,b:Long):Long = {
    assert(a > 0 && b >= 0)
    if (b == 0) {
      a
    } else {
      gca(b, a % b)
    }
  }



  //尾递归求n!
  def factorial(init:Long,n:Long):Long = {
    if (n == 0) {
      init
    }else {
      factorial(n * init,n-1)
    }
  }

  //求x的n次方摸m
  def mod(x:Long,n:Long,m:Long):Long = n match {
    case 0 => 1
    case 1 => x % m
    case k if k % 2 == 0 =>
      val a = mod(x,k / 2,m)
      a * a % m
    case k if k % 2 == 1 =>
      val a = mod(x,k / 2,m)
      (a * a * (x % m)) % m
  }

  //线性递归搜索
  def search[T](ts:Seq[T],key:T):Int = {
    val n = ts.size
    def _search(i:Int):Int = {
      if (ts(i) == key) {
        i
      } else {
        if (i == n-1) {
          -1
        }else {
          _search(i+1)
        }
      }
    }
    _search(0)
  }

  trait Comp[T] {
    //如果前者比后者小返回小
    def less(elem1:T,elem2:T):Boolean
  }

  object Comp {
    implicit val intComp:Comp[Int] = (elem1: Int, elem2: Int) => elem1 <= elem2
  }

  //考虑怎么不用递归
  def merge[T:Comp](ts1:Seq[T],ts2:Seq[T]):Seq[T] = {
    if (ts1.isEmpty) {
      ts2
    } else {
      if (ts2.isEmpty) {
        ts1
      } else {
        val comp = implicitly[Comp[T]]
        if (comp.less(ts1.head,ts2.head)) {
          ts1.head +: merge(ts1.tail,ts2)
        } else {
          ts2.head +: merge(ts1,ts2.tail)
        }
      }
    }
  }

  def mergeSort[T:Comp](ts:Seq[T]):Seq[T] = {
    if (ts.isEmpty || ts.size == 1) {
      ts
    } else {
      merge(mergeSort(ts.slice(0,ts.length/2)),mergeSort(ts.slice(ts.length/2,ts.length)))
    }
  }

  def quickSort[T:Comp](ts:Seq[T]):Seq[T] = {
    if (ts.isEmpty || ts.size == 1) {
      ts
    } else {
      val (left,head,right) = split(ts)
      (quickSort(left) :+ head) ++: quickSort(right)
    }
  }

  def split[T:Comp](ts:Seq[T]):(Seq[T],T,Seq[T]) = {
    assert(ts.nonEmpty)
    val comp = implicitly[Comp[T]]
    if (ts.size == 1){
      (Seq.empty,ts.head,Seq.empty)
    }else {
      (ts.tail.filter(t => comp.less(t,ts.head)),ts.head, ts.tail.filter(t => !comp.less(t,ts.head)))
    }
  }


}
