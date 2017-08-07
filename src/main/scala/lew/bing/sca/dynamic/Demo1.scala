package lew.bing.sca.dynamic

import scala.collection.mutable
import scala.language.dynamics

//import scala.

/**
  * Created by 刘国兵 on 2017/4/30.
  */
object Demo1 {

  def main(args: Array[String]): Unit = {
    val i = new WrapperMapper[Int](mutable.Map("love" -> 2))
    println(i.love)
    i.hate = 5
    println(i.hate)
  }


  class WrapperMapper[M](map: mutable.Map[String,M]) extends Dynamic{

    def selectDynamic(name:String):Option[M] = map.get(name)

    def updateDynamic(name:String)(value:M): Option[M] = map.put(name,value)

  }

}
