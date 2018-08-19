package lew.bing.nlp

import breeze.linalg.{DenseVector, norm}
import breeze.optimize.linear._

/**
  * Created by 刘国兵 on 2018/8/18.
  */
object Linear {

  def main(args: Array[String]): Unit = {
    val lp = new LinearProgram()
    import lp._

    val x0 = Real()
    val x1 = Real()
    val x2 = Real()
    val lpp =  ( (x0 +  x1 * 2 + x2 * 3 )
      subjectTo ( x0 * -1 + x1 + x2 <= 20)
      subjectTo ( x0 - x1 * 3 + x2 <= 30)
      subjectTo ( x0 <= 40 )
      )

    println(maximize(lpp))
  }

}
