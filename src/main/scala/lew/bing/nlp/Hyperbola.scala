package lew.bing.nlp

import breeze.linalg._
import breeze.numerics._
import breeze.plot.{Figure, plot}


/**
  * Created by 刘国兵 on 2018/8/19.
  */
object Hyperbola {

  def main(args: Array[String]): Unit = {
    val s = linspace(- 7.0 * Math.PI / 16.0, 7.0 * Math.PI / 16.0, 10)
    val s2 = s +:+ Math.PI
    val x = 1.0 /:/ cos(s)
    val y = tan(s)
    val x2 = 1.0 /:/ cos(s2)
    val y2 = tan(s2)
    val f = Figure()
    val p = f.subplot(0)
    p += plot(x, y)
    p += plot(x2, y2)
    p.xlabel = "x axis"
    p.ylabel = "y axis"
    p.setXAxisDecimalTickUnits()
    p.setYAxisDecimalTickUnits()
    f.saveas("hyperbola.png")
  }
}
