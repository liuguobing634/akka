package lew.bing.nlp

import breeze.linalg._
import breeze.numerics.{cos, sin}
import breeze.plot._

/**
  * Created by 刘国兵 on 2018/8/18.
  */
object Circle {

  def main(args: Array[String]): Unit = {
    val f = Figure()
    val p = f.subplot(0)
    val x = linspace(0.0,2 * Math.PI,100)
    p += plot(x.map(k => cos(k)),x.map(k => sin(k)))
    p.xlabel = "x axis"
    p.ylabel = "y axis"
    p.setXAxisDecimalTickUnits()
    p.setYAxisDecimalTickUnits()
    p.legend = true
    f.saveas("circle.png")
  }

}
