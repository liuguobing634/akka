package lew.bing.forImplicit

/**
  * Created by 刘国兵 on 2018/5/20.
  */
object TestImplicit {

  def main(args: Array[String]): Unit = {
    implicit object DoubleToInt extends ToInt[Double] {
      override def apply(t: Double): Int = t.intValue()
    }
    println(plus(1.2,2.3))

  }

  def plus(a1: Int,a2: Int): Int = {
    a1 + a2
  }

  trait ToInt[T]  {
    def apply(t: T):Int
  }

  implicit def canBeInt[T](t:T)(implicit convert: ToInt[T]):Int = {
    convert(t)
  }


  implicit def plusInt: Plus[Int] = new Plus[Int] {
    override def plus(a: Int, b: Int): Int = a + b
  }


  trait Plus[A] {
    def plus(a:A,b:A):A
  }

  def sum[A](list:List[A])(implicit monad: Plus[A]): A = {
    list.reduce(monad.plus)
  }



  def sayHello()(implicit name: String):Unit = {
    println(s"$name say hello")
  }




}
