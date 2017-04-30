package lew.bing.scala.`macro`
import scala.collection.mutable.ListBuffer
import scala.language.experimental.macros
import scala.reflect.macros.{blackbox, whitebox}

/**
  * Created by 刘国兵 on 2017/4/29.
  */
object TestImpl {
  def _println[T:c.WeakTypeTag](c:blackbox.Context)(cond:c.Expr[T]):c.Expr[Unit] = {
    import c.universe._
    val Literal(Constant(v:Int)) = cond.tree
    //这样写并不会打印这个值，应该做点修改
    c.Expr[Unit](q"""${reify(println(cond.splice)).tree}""")
  }
}

class PrintA[T] {
  def myPrint(cond:T):Unit = macro TestImpl._println[T]
}
