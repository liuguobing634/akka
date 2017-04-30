package lew.bing.spiders
import scala.util.matching.Regex
/**
  * Created by 刘国兵 on 2017/4/29.
  */
object Demo2 {

  def main(args: Array[String]): Unit = {
    val content = """<a href="http://www.baidu.com">百度</a> """
    val pattern = """<a href="([^"]+)">([^<]+)</a>""".r
    pattern.findFirstMatchIn(content).foreach{s =>
      println(s.group(1))
      println(s.group(2))
    }
  }

}
