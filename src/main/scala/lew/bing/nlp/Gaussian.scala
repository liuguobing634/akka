package lew.bing.nlp

import breeze.plot._
import breeze.stats.distributions._
import breeze.linalg._

/**
  * Created by 刘国兵 on 2018/8/18.
  */
object Gaussian {

  def main(args: Array[String]): Unit = {
    val g = breeze.stats.distributions.Gaussian(0.0,1.0)

    val s = linspace(-2,2,100)
    val k = hist(g.sample(100000),100)
    val f = Figure()
    val p = f.subplot(0)
    p += k
    p.xlabel = "x axis"
    p.ylabel = "y axis"
    p.setXAxisDecimalTickUnits()
    p.setYAxisDecimalTickUnits()
    f.saveas("gaussion.png")
  }

}
