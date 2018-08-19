package lew.bing.nlp

import breeze.stats.distributions.{Binomial, Poisson}

/**
  * Created by 刘国兵 on 2018/8/18.
  */
object Distrib {

  def main(args: Array[String]): Unit = {
    testPoisson()
    testBinomial()
    val s = (1 to 10).product.toDouble
    val k = (1 to 3).product.toDouble
    val l = (1 to 7).product.toDouble
    val p = Math.pow(2, 10)
    println(s/(k*l*p))
  }

  def testPoisson(): Unit = {
    val poi = Poisson(3.0)
    val s = poi.sample(30)
    val p = s.map {poi.probabilityOf}
    println(s)
    println(p)
  }

  def testBinomial(): Unit = {
    val binomial = Binomial(10,0.5)
    val b = binomial.sample(10)
    val p = b.map {n =>
      (n, binomial.probabilityOf(n))
    }
    println(p)
    val mean = binomial.mean
    println(s"mean is $mean")
    val variance = binomial.variance
    println(s"variance is $variance")
  }

}
