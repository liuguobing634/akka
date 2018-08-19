package lew.bing.alog

/**
  * Created by 刘国兵 on 2018/5/24.
  */
object MergeSort {

  def main(args: Array[String]): Unit = {
    println(merge(List(1,3,6),List(2,4,5)))
    println(sort(List(4,3,2,5,6,7)))
  }

  def sort(l: List[Int]):List[Int] = {
    l.size match {
      case 0 => l
      case 1 => l
      case _ =>
        val left = l.slice(0,l.size/2)
        val right = l.slice(l.size / 2, l.size)
        merge(sort(left),sort(right))
    }
  }

  def merge(l1:List[Int],l2:List[Int]):List[Int] = {
    val l3 = Int.MinValue +: l1
    val z:List[(Int,Int)] = l3.zip(l1)
    var i = 0
    val k = z.flatMap {
      case (a, b) =>
        var j = i
        while (j < l2.size && l2(j) < b  ) {
          j += 1
        }
        val s = l2.slice(i,j)
        i = j
        s :+ b
    }
    k ++ l2.slice(i, l2.size)
  }


}
