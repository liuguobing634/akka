import breeze.linalg._


val a = DenseVector(1,2,4,5,6)
val b = DenseVector(4,5,6,7,8)

val c = a + b

val res = a dot b

val m = max(c)

val d = c *:* 2

val su = sum(a)

argmax(a)


import breeze.stats.mean
val x = DenseMatrix((1,2,3),(4,5,6))

x(::, *) + DenseVector(1,2)

mean(x(*, ::))

