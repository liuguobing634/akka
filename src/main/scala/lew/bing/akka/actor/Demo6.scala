package lew.bing.akka.actor

import akka.actor.Props
import akka.typed._
import akka.typed.scaladsl.Actor._
import akka.util.Timeout

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.concurrent.Await

/**
  * Created by 刘国兵 on 2017/4/18.
  */
object Demo6 {



  object ChatRoom {
    sealed trait Command
    final case class GetSession(screenName: String,replyTo:ActorRef[SessionEvent]) extends Command

    sealed trait SessionEvent
    final case class SessionGranted(handle: ActorRef[PostMessage]) extends SessionEvent
    final case class SessionDenied(reason:String) extends SessionEvent
    final case class MessagePosted(screenName:String,message:String) extends SessionEvent

    final case class PostMessage(message:String)

    private final case class PostSessionMessage(screenName:String,message:String) extends Command

    def chatRoom(sessions:List[ActorRef[SessionEvent]] = List.empty):Behavior[Command] =
      Stateful[Command] { (ctx,msg) =>
        msg match {
          case GetSession(screenName, client) ⇒
            val wrapper = ctx.spawnAdapter {
              p: PostMessage ⇒ PostSessionMessage(screenName, p.message)
            }
            client ! SessionGranted(wrapper)
            chatRoom(client :: sessions)
          case PostSessionMessage(screenName, message) ⇒
            val mp = MessagePosted(screenName, message)
            sessions foreach (_ ! mp)
            Same
        }
      }

  }


  def main(args: Array[String]): Unit = {

    import ChatRoom._
    val gabbler: Behavior[SessionEvent] =
      Stateful[SessionEvent] { (_, msg) ⇒
        msg match {
          case SessionDenied(reason) ⇒
            println(s"cannot start chat room session: $reason")
            Stopped
          case SessionGranted(handle) ⇒
            handle ! PostMessage("Hello World!")
            Same
          case MessagePosted(screenName, message) ⇒
            println(s"message has been posted by '$screenName': $message")
            Stopped
        }
      }

    val main: Behavior[akka.NotUsed] =
      Stateful(
        behavior = (_, _) => Unhandled,
        signal = { (ctx, sig) =>
          sig match {
            case PreStart =>
              val chatRoom = ctx.spawn(ChatRoom.chatRoom(), "chatroom")
              val gabblerRef = ctx.spawn(gabbler, "gabbler")
              ctx.watch(gabblerRef)
              chatRoom ! GetSession("ol’ Gabbler", gabblerRef)
              Same
            case Terminated(ref) =>
              Stopped
            case _ =>
              Unhandled
          }
        })

    val system = ActorSystem("ChatRoomDemo",main)
    Await.result(system.whenTerminated,1.second)
  }
}
