import breeze.stats.distributions.{Binomial, Poisson}

val b = Binomial(100, 0.3)

b.sample(100)

val p = Poisson(2.5)
val s = p.sample(100)

s.map {p.probabilityOf(_)}
