package lew.bing.spiders

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest}
import akka.stream.ActorMaterializer

import scala.io.StdIn

/**
  * Created by 刘国兵 on 2017/8/26.
  */
object JuanTu {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("spider-demo1")
    implicit val executionContext = system.dispatcher
    implicit val materializer = ActorMaterializer()
    implicit val http = Http(system)
//    http.singleRequest(HttpRequest(method = HttpMethods.PUT, uri="http://localhost:9200/megacorp/employee/2", entity = HttpEntity(ContentTypes.`application/json`,
//      """
//        |{
//        |    "first_name" : "lgb",
//        |    "last_name" :  "Sb",
//        |    "age" :        29,
//        |    "about" :      "I love to go rock climbing",
//        |    "interests": [ "sports", "music" ]
//        |}
//      """.stripMargin))).foreach(println)
    http.singleRequest(HttpRequest(uri = "http://localhost:9200/megacorp/employee/_search", entity = HttpEntity(ContentTypes.`application/json`,
      """
        |{
        |    "query" : {
        |        "match" : {
        |            "last_name" : "Smith"
        |        }
        |    }
        |}
      """.stripMargin))).foreach(println)
    StdIn.readLine()
    system.terminate()
  }

}
