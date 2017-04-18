package lew.bing.scala

/**
  * Created by 刘国兵 on 2017/4/16.
  */
object ImplicitTest {

  def main(args: Array[String]): Unit = {

    implicit def IntConvertMyInt(value:Int):MyInt = new MyInt(value)

    println(2 ** 3)
    println(-2 ** 3)
    println(0 ** 3)
    println(2 ** -3)

    println(square(2))
    println(square(7))

    implicit object IntMonoid extends Monoid[Int] {
      val unit = 0
      override def add(x: Int, y: Int): Int = x + y
    }

    implicit object StringMonnit extends Monoid[String] {
      val unit = ""

      override def add(x: String, y: String): String = x concat y
    }

    println(sum(List(2,3,4,8,9)))
    println(sum(List("a","b","c")))

//    import Monoid.monoid



    println(sum(List(1.2,2.3,3.4,-2.3)))// 无法编译，报错是找不到implicit value给参数monoid,没有足够的参数

    println(sum(List(1.2,2.3,3.4,-2.3))(new Monoid[Double] {
      val unit = 0.0
      override def add(x: Double, y: Double): Double = {
        if (y < 0) x else x + y
      }
    }))//6.9

  }


  def sum[T](seq:Seq[T])(implicit monoid:Monoid[T]):T ={
    if (seq.isEmpty) monoid.unit
    else monoid.add(seq.head,sum(seq.tail))
  }

  abstract class SemiGroup[A] {
    def add(x:A,y:A): A
  }

  abstract class Monoid[A] extends SemiGroup[A] {
    def unit:A
  }

  object Monoid {
    implicit val monoid:Monoid[Double] = new Monoid[Double] {
      override def unit: Double = 0.0

      override def add(x: Double, y: Double): Double = {
        x + y
      }
    }
  }

  def square(value:MyInt):Double =  {
    value ** 2
  }

  class MyInt(value:Int) {

    def **(times: Int): Double = times match {
      case a if a < 0 => if (value == 0) throw new Exception("0没有非负指数") else 1.0 / (this ** (-times))
      case 0 => if (value == 0) throw new Exception("0没有非负指数") else 1
      case a => (1 to a).map(_ => value).product.toDouble
    }

  }

}
