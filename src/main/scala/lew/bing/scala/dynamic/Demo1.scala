package lew.bing.scala.dynamic

import scala.language.dynamics

//import scala.

/**
  * Created by 刘国兵 on 2017/4/30.
  */
object Demo1 {

  def main(args: Array[String]): Unit = {
    val i = new WrapperMapper[Int](Map("love" -> 2))
    println(i.love)
  }


  class WrapperMapper[M](map: Map[String,M]) extends Dynamic{

    def selectDynamic(name:String):Option[M] = map.get(name)

  }

}
