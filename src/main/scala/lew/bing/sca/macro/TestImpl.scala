package lew.bing.sca.`macro`
import scala.collection.mutable.ListBuffer
import scala.language.experimental.macros
import scala.reflect.macros.{blackbox, whitebox}

/**
  * Created by 刘国兵 on 2017/4/29.
  */
object TestImpl {
  def _println[T:c.WeakTypeTag](c:blackbox.Context)(cond:c.Tree) = {
    import c.universe._
//    val Literal(Constant(v:Int)) = cond.tree
    //这样写编译时就会打印，而不是运行时
//    q"""${println(cond)}"""
    //这样才起作用
    q"""println($cond)"""
  }
}

class PrintA[T] {
  def myPrint(cond:T):Unit = macro TestImpl._println[T]
}
