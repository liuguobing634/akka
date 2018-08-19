package lew.bing.jsoup

import java.io.{BufferedReader, InputStreamReader}
import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{FileIO, Flow}
import org.jsoup.Jsoup
import scala.collection.JavaConverters


/**
  * Created by 刘国兵 on 2017/8/23.
  */
object Demo1 {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("jsoup")
    implicit val ec = system.dispatcher
    implicit val materializer = ActorMaterializer()
    val http = Http(system)


    val transver = Flow[String].map {str =>
      println(str)
      val jsoup = Jsoup.parse(str)
      val k = jsoup.select(".article li a")
      JavaConverters.asScalaIterator(k.iterator()).map {elem =>
        s"http://www.208xs.com${elem.attr("href")}"
      }
    }

    val req = http.singleRequest(HttpRequest(uri = "http://www.208xs.com/dingdian/0_296/"))
    req.foreach {
      case HttpResponse(StatusCodes.OK, headers, entity, _) =>
        entity.dataBytes.fold(""){
          case (init, next) =>
            init + next.utf8String
        }.via(transver).runForeach(k => {

          k.foreach(println)
        })
      case _ =>
        println("abc")
    }

    val s = new BufferedReader(new InputStreamReader(System.in)).readLine()
//    Thread.sleep(1000)
    println(s)

    system.terminate()
  }

}
