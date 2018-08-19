package lew.bing.math

/**
  * Created by 刘国兵 on 2017/6/18.
  */
object QuarterNumbers {

  def main(args: Array[String]): Unit = {
//    println(quartNumbers(List(1,2,3,4,5,6,7)))
    val evenNumbers = (1 to 10).map(_*2)
    evenNumbers.foreach(println)

  }

  def quartNumbers(numbers: List[Double]):(Double,Double,Double) = {
    val sortedNumber = numbers.sorted
    val size = sortedNumber.size
    val firstPosition = size + 1
    (takeNumber(firstPosition,4,sortedNumber),
      takeNumber(firstPosition*2,4,sortedNumber),
      takeNumber(firstPosition*3,4,sortedNumber))

  }
  def takeNumber(p:Int,dev:Int,numbers: List[Double]):Double = p % dev match {
    case 0 => numbers(p / dev - 1)
    case n =>
      val num1 = numbers(p / dev -1)
      val num2 = numbers(p / dev )
      num1 * n / dev + num2 * (dev - n) / dev
  }


}
