package lew.bing.alog

/**
  * Created by 刘国兵 on 2018/5/23.
  */
object QuickSort {

  def main(args: Array[String]): Unit = {
    println(sort(List(1,2,6,5,7,9,10)))
  }

  def sort(a: List[Int]):List[Int] = {
    a.size match {
      case 0 => a
      case 1 => a
      case _ =>
        (sort(a.tail.filter(b => b <= a.head)) :+ a.head) ++ sort(a.tail.filter(b => b > a.head))
    }

  }

}
