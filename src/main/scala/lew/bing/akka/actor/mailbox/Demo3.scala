package lew.bing.akka.actor.mailbox

import java.util.concurrent.ConcurrentLinkedQueue

import akka.actor.AbstractActor.Receive
import akka.actor.{ActorRef, ActorSystem}
import akka.dispatch.{Envelope, MailboxType, MessageQueue, ProducesMessageQueue}
import com.typesafe.config.Config

/**
  * Created by 刘国兵 on 2017/4/28.
  */
object Demo3 {

  trait MyUnboundedMessageQueueSemantics

  object MyUnboundedMailbox {

    class MyMessageQueue extends MessageQueue with MyUnboundedMessageQueueSemantics {
      private final val queue = new ConcurrentLinkedQueue[Envelope]()

      def enqueue(receive: ActorRef,handle: Envelope):Unit = {
        queue.offer(handle)
      }

      def dequeue():Envelope = queue.poll()

      def numberOfMessages:Int = queue.size()
      def hasMessages: Boolean = !queue.isEmpty
      def cleanUp(owner:ActorRef,deadLetters: MessageQueue): Unit = {
        while (hasMessages) {
          deadLetters.enqueue(owner, dequeue())
        }
      }

    }



  }

  class MyUnboundedMailbox extends MailboxType with ProducesMessageQueue[MyUnboundedMailbox.MyMessageQueue]{
    import MyUnboundedMailbox._

    def this(settings:ActorSystem.Settings,config:Config) {
      this()
    }

    final override def create(owner: Option[ActorRef], system: Option[ActorSystem]): MessageQueue = {
      new MyMessageQueue()
    }
  }

}
