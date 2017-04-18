package lew.bing.scala

/**
  * Created by 刘国兵 on 2017/4/16.
  */
object ValueClassTest {

  implicit class RichInt(val self:Int) extends AnyVal {
    def myToHexString:String = java.lang.Integer.toHexString(self)
  }

  def main(args: Array[String]): Unit = {

    println(99.myToHexString)
    implicit val a = "a"
    println("123".isNumber)
    println("123".trim)
    implicit class NewString(val string: String)(implicit val a: String) {

      def isNumber:Boolean = {
        string.matches("\\d+")
      }

      def trim:String = string.trim+"a"

    }
  }





}
