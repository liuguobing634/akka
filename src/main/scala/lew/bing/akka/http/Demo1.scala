package lew.bing.akka.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.{Directive, RequestContext, Route, RouteResult}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow

import scala.io.StdIn

/**
  * Created by 刘国兵 on 2017/4/19.
  */
object Demo1 {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("my-http")
    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher



    val route:Route =
      path("hello"){
        get {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`,"<h1>Say hello to akka-http</h1>"))
        }
      }
    val map = Flow[RequestContext].map(route)
    //暂时不可用，不知道为何
    val bindingFuture = Http().bindAndHandle(route,"localhost",9898)
    println(s"Server online at http://localhost:9898/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }

}
