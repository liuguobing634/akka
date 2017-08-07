package lew.bing.sca

/**
  * Created by 刘国兵 on 2017/6/9.
  */
object MatrixStep {

  def main(args: Array[String]): Unit = {
    val a = kindsOfSteps(6,8)
    //计算4,6
    val b = kindsOfSteps(4,6)
    //从4,6到6,8用了6步
    println(a - b * kindsOfSteps(3,3))
  }


  def kindsOfSteps(x:Int,y:Int):Int = {
    if (y < x) {
      kindsOfSteps(y,x)
    } else {
      if (x == 1) {
        1
      } else {
        kindsOfSteps( x -1,y) + kindsOfSteps(x , y - 1)
      }
    }
  }

}
