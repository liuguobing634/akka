package lew.bing.scala.`macro`

import scala.reflect.macros.blackbox
import scala.language.experimental.macros


/**
  * Created by 刘国兵 on 2017/4/30.
  */
object Debug {

  def apply[T](x: => T):T = macro impl
  def impl(c:blackbox.Context)(x:c.Tree) = {
    import c.universe._
    val q"..$stats" = x
    val loggedStats =  stats.flatMap {stat =>

      val msg = "executing " + showCode(stat)
      List(q"println(${msg})",stat)
    }
    q"..$loggedStats"
  }

}
