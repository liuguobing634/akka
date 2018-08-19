import breeze.linalg._
import breeze.stats.distributions.{Binomial, Poisson}
val x = DenseVector.zeros[Double](5)
x(0)
x(1) = 2
x(1)
x(3 to 4) := 0.5
x
x(1)
val m = DenseMatrix.zeros[Int](5,5)

(m.rows, m.cols)

val p = Poisson(3.0)

p.sample(10)

val b = Binomial(10, 0.5)
b.sample(3)
