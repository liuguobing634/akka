import breeze.stats.distributions._
import breeze.stats._

val p = Poisson(3.0)

val samp = p.sample(10)

val posi = samp.map(p.probabilityOf)

val doublePoi = for(x <- p) yield x.toDouble

meanAndVariance(doublePoi.samples.take(1000))

p.mean

p.variance
