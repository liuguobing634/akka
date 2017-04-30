package lew.bing.scala.`macro`
import scala.language.experimental.macros


/**
  * Created by 刘国兵 on 2017/4/29.
  */
object Demo1 {

  def main(args: Array[String]): Unit = {
//    val printA = new PrintA[Int]
//    printA.myPrint(2)
    Debug {
      val a = 1
      val b = a + 2
    }
  }

}
