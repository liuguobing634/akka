package lew.bing.akka.http

import java.util.Scanner

import akka.actor._
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}

import scala.concurrent.ExecutionContext
import akka.http.scaladsl.server.Directives._

import scala.io.StdIn


/**
  * Created by 刘国兵 on 2017/8/6.
  */
object WebSocketDemo2 {


  implicit val system = ActorSystem("my-websocket")
  implicit val materializer = ActorMaterializer()
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext:ExecutionContext = system.dispatcher

  // 创建新的route，接受一个actorRef => prop



  object ActorFlow {
    // the target actorRef should receive a message
    def fromActorRef(request: HttpRequest, pros: ActorRef => ActorRef): Flow[Message,Message,Any] = {
      val (outActor,publisher) = Source.actorRef(1,OverflowStrategy.dropNew).toMat(Sink.asPublisher(false))(Keep.both).run()
      val inActor = pros(outActor)
      inActor ! Joined(request,outActor)
      val in = Sink.actorRef[Message](system.actorOf(Props(new Actor {
        context.watch(outActor)
        context.watch(inActor)
        override def receive: Receive = {
          case Terminated(a) if a == outActor => inActor ! Left(outActor)
          case Terminated(a) if a == inActor =>
            // 如果in关闭了，那么就关闭自身
            context.stop(self)
          case other:Message =>

            inActor ! ReceiveMsg(other,outActor)
          case other =>
            println(other)
        }
      })),Status.Success(1))
      Flow.fromSinkAndSource(in,Source.fromPublisher(publisher))
    }

  }

  // 创建几个事件
  sealed trait Msg
  case class ReceiveMsg(msg: Message, actorRef: ActorRef)
  case class Joined(request: HttpRequest,actorRef: ActorRef)
  case class Left(actorRef: ActorRef)


  def main(args: Array[String]): Unit = {
    val inActor = system.actorOf(Props[InActor])
    val route = get{
      pathEndOrSingleSlash {
        getFromResource("index.html")
      } ~ path("ws"){
        extractRequest {request =>
          handleWebSocketMessages(ActorFlow.fromActorRef(request,{actor =>
            inActor
          }))
        }
      }
    }
    val bindingFuture = Http().bindAndHandle(route,"localhost",9892)
    println(s"Server online at http://localhost:9898/\nPress RETURN to stop...")
//    StdIn.readLine() // let it run until user presses return
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done

  }

  class InActor extends Actor {

    var actors: Set[(HttpRequest,ActorRef)] = Set.empty

    override def receive: Receive = {
      case outActor: ActorRef =>
        actors = actors.filterNot(_._2 == outActor)
      case Joined(request,actorRef) =>
        actorRef ! TextMessage.Strict("你好,欢迎光临")
        actors.foreach(_._2 ! TextMessage.Strict("来了新朋友"))
        actors += request -> actorRef
      case ReceiveMsg(msg,actorRef) =>
        actorRef ! msg
    }
  }
}
