package lew.bing.akka.actor

import akka.actor.Props
import akka.typed._
import akka.typed.scaladsl.Actor
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

    val behavior: Behavior[Command] =
      chatRoom(List.empty)

    def chatRoom(sessions:List[ActorRef[SessionEvent]] = List.empty):Behavior[Command] =
      Actor.immutable[Command] { (ctx,msg) =>
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
            Actor.same
        }
      }

  }


  def main(args: Array[String]): Unit = {

    import ChatRoom._
    val gabbler: Behavior[SessionEvent] =
      Actor.immutable[SessionEvent] { (_, msg) ⇒
        msg match {
          case SessionDenied(reason) ⇒
            println(s"cannot start chat room session: $reason")
            Actor.stopped
          case SessionGranted(handle) ⇒
            handle ! PostMessage("Hello World!")
            Actor.same
          case MessagePosted(screenName, message) ⇒
            println(s"message has been posted by '$screenName': $message")
            Actor.stopped
        }
      }

    val main: Behavior[akka.NotUsed] =
      Actor.deferred { ctx ⇒
        val chatRoom = ctx.spawn(ChatRoom.behavior, "chatroom")
        val gabblerRef = ctx.spawn(gabbler, "gabbler")
        ctx.watch(gabblerRef)
        chatRoom ! GetSession("ol’ Gabbler", gabblerRef)

        Actor.immutable[akka.NotUsed] {
          (_, _) ⇒ Actor.unhandled
        } onSignal {
          case (ctx, Terminated(ref)) ⇒
            Actor.stopped
        }
      }

    val system = ActorSystem("ChatRoomDemo",main)
    Await.result(system.whenTerminated,1.second)
  }
}
