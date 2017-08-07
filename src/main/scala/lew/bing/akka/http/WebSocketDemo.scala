package lew.bing.akka.http

import akka.NotUsed
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, PoisonPill, Props, Status, Terminated}
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Directives._
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.stream.javadsl.Sink
import akka.stream.scaladsl.{Flow, Source}

import scala.concurrent.ExecutionContext

/**
  * Created by 刘国兵 on 2017/8/6.
  */
object WebSocketDemo {

  implicit val system = ActorSystem("my-websocket")
  implicit val materializer = ActorMaterializer()
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext:ExecutionContext = system.dispatcher
  val chat:Chat = Chat()


  def chatFlow(name: String): Flow[Message,Message,Any]= {
    val s: Flow[Message,String,NotUsed] = Flow[Message].collect {
      case TextMessage.Strict(msg) =>
        // 接受json信息,通过json信息来反馈针对谁发送消息
        msg
    }
    s.via(chat.chatFlow(name)).map{
      case Protocol.ChatMsg(sender,msg,None) =>
        s"""{"type":"message","msg":${safeJsonValue(msg)},"sender":${safeJsonValue(sender)}}"""
      case Protocol.ChatMsg(sender,msg,Some(to)) =>
        s"""{"type":"message","msg":${safeJsonValue(msg)},"sender":${safeJsonValue(sender)},"to":${safeJsonValue(to)}}"""
      case Protocol.Joined(who,members) =>
        s"""{"type":"online","who":${safeJsonValue(who)},"members":${membersStr(members)}}"""
      case Protocol.Left(who,members) =>
        s"""{"type":"offline","who":${safeJsonValue(who)},"members":${membersStr(members)}}"""
    }.map(TextMessage.Strict)

  }

  def safeJsonValue(msg: String) : String = {
    s"${msg.replace("\"","\\\"")}"
  }

  def membersStr(members: Seq[String]):String = {
    "[" +
      members.reduceOption({(op,op2) =>
        s"""${safeJsonValue(op)},${safeJsonValue(op2)}"""
      }).getOrElse("") + "]"
  }

  val wbSocketRoute = path("ws") {
    parameters('name.as[String])(name =>
      // 需要一个flow[Message,Message,Any]
      // 握手升级协议的时候是注册信息
      // 每个name应该只有一个通道
      handleWebSocketMessages(chatFlow(name)))
  }

}

trait Chat {
  def chatFlow(sender: String): Flow[String,Protocol.Msg,Any]
  def injectMessage(message: Protocol.ChatMsg): Unit
  def sendToAdmin(message: Any)
}

object Chat {
  def apply()(implicit system: ActorSystem): Chat = {
    val chatActor = system.actorOf(Props(new Actor {

      var allMembers: Map[String, ActorRef] = Map.empty

      override def receive: Receive = {
        case Up(who,actor) =>
          allMembers += who -> actor
          // 发送消息
          context.watch(actor)
          send(Protocol.Joined(who,allMembers.keys.toList))
        case Down(who) =>
          // 告诉匹配的actorRef已经完成了
          allMembers.filter(_._1 == who).foreach(_._2 ! Status.Success(Unit))
          allMembers = allMembers.filterNot(_._1 == who)
          // 通知大家有人下线
          send(Protocol.Left(who,allMembers.keys.toList))
        case msg:Protocol.ChatMsg =>
          send(msg)
        case msg: ReceiveMsg =>
          send(msg.toChatMsg)
        case Terminated(actor) =>
          // 如果有actor死亡了
          allMembers = allMembers.filterNot(_._2 == actor)
      }
      private def send(msg: Protocol.Msg) = allMembers.foreach(_._2 ! msg)
    }))
    // 创建一个管理员actor
    val adminActor = system.actorOf(Props(new Actor with ActorLogging{
      override def receive: Receive = {
        case ReceiveMsg(sender,msg,_) =>
          log.info("用户{}发送了一条消息:{}",sender,msg)
        case Up(who: String,_) =>
          log.info("用户{}上线",who)
        case Down(who:String) =>
          log.info("用户{}下线",who)
      }
    }))
    chatActor ! ("admin",adminActor)

    new Chat {
      override def chatFlow(sender: String): Flow[String, Protocol.Msg, Any] = {
        val in = Flow[String].map {message =>
          // 对message进行分析
          // 有@{}则提取出来
          val pattern = raw"@{(^\}+)}".r
          ReceiveMsg(message,sender)
        }.to(Sink.actorRef(chatActor,Down(sender)))
        val out = Source.actorRef[Protocol.ChatMsg](1,OverflowStrategy.fail)
          .mapMaterializedValue(chatActor ! Up(sender,_))
        Flow.fromSinkAndSource(in,out)
      }

      override def injectMessage(message: Protocol.ChatMsg): Unit = chatActor ! message

      override def sendToAdmin(message: Any): Unit = adminActor ! message
    }

  }

  private case class Up(who: String,actor: ActorRef)
  private case class Down(who: String)
  private case class ReceiveMsg(message: String, sender: String, to: Option[String] = Option.empty) {
    def toChatMsg: Protocol.ChatMsg = Protocol.ChatMsg(sender,message,to)
  }

}

object Protocol {
  sealed trait Msg
  // 如果to不是none，则是@，前端加上特别提示
  case class ChatMsg(sender:String,msg: String,to: Option[String]) extends Msg
  // 上下线都通知目前成员人数
  case class Left(who: String, members: Seq[String]) extends Msg
  case class Joined(who: String, members: Seq[String]) extends Msg
}


