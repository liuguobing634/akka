package lew.bing.akka.stream

import java.nio.ByteOrder

import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl.{BidiFlow, Flow, GraphDSL, Sink, Source}
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}
import akka.util.ByteString

import scala.concurrent.Await
import scala.concurrent.duration._


/**
  * Created by 刘国兵 on 2017/4/14.
  */
object Demo10 {

  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem("QuickStart")
    implicit val materializer = ActorMaterializer()
    import scala.concurrent.ExecutionContext.Implicits.global
    implicit val order = ByteOrder.LITTLE_ENDIAN

    trait Message
    case class Ping(id:Int) extends Message
    case class Pong(id:Int) extends Message

    def toBytes(msg: Message):ByteString = msg match {
      case Ping(id) => ByteString.newBuilder.putByte(1).putInt(id).result()
      case Pong(id) => ByteString.newBuilder.putByte(2).putInt(id).result()
    }

    def fromBytes(bytes: ByteString):Message = {
     implicit val order = ByteOrder.LITTLE_ENDIAN
      val id = bytes.iterator
      id.getByte match {
        case 1 => Ping(id.getInt)
        case 2 => Pong(id.getInt)
        case other => throw new RuntimeException(s"解析失败，期待1或者2，但得到的是$other")
      }

    }

    def codecVerbose = BidiFlow.fromGraph(GraphDSL.create(){b =>
      val outbound = b.add(Flow[Message].map(toBytes))
      val inbound = b.add(Flow[ByteString].map(fromBytes))
      BidiShape.fromFlows(outbound,inbound)
    })

    val framing = BidiFlow.fromGraph(GraphDSL.create(){b =>
      implicit val order = ByteOrder.LITTLE_ENDIAN

      def addLengthHeader(bytes:ByteString) = {
        val len = bytes.length
        ByteString.newBuilder.putInt(len).append(bytes).result()
      }
      class FrameParser extends GraphStage[FlowShape[ByteString,ByteString]] {
        val in = Inlet[ByteString]("FrameParser.in")
        val out = Outlet[ByteString]("FrameParser.out")
        override val shape = FlowShape.of(in,out)

        override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new GraphStageLogic(shape) {
          var stash = ByteString.empty
          var needed = -1

          setHandler(out,new OutHandler {
            override def onPull(): Unit = {
              if (isClosed(in)) run()
              else pull(in)
            }
          })
          setHandler(in,new InHandler {
            override def onPush(): Unit = {
              val bytes = grab(in)
              stash = stash ++ bytes
              run()
            }

            override def onUpstreamFinish(): Unit = {
              if (stash.isEmpty) completeStage()
              else if (isAvailable(out)) run()
            }
          })
          private def run():Unit ={
            if (needed == -1){
              if (stash.length < 4){
                if (isClosed(in)) completeStage()
                else pull(in)
              }else {
                needed = stash.iterator.getInt
                stash = stash.drop(4)
                run()
              }
            }else if (stash.length < needed){
              if (isClosed(in)) completeStage()
              else pull(in)
            }else {
              val emit = stash.take(needed)
              stash = stash.drop(needed)
              needed = -1
              push(out,emit)
            }
          }
        }
      }
      val outbound = b.add(Flow[ByteString].map(addLengthHeader))
      val inbound = b.add(Flow[ByteString].via(new FrameParser))
      BidiShape.fromFlows(outbound,inbound)
    })
    val stack = codecVerbose.atop(framing)
    val pingpong = Flow[Message].collect{ case Ping(id) => Pong(id)}
    //flow成为了一个标准的flow了，通过一个反向
    val flow = stack.atop(stack.reversed).join(pingpong)
    val result = Source(0 to 9).map(Ping).via(flow).limit(20).runWith(Sink.seq)
    println (Await.result(result,1.second))

    //bidiFlow两个出口两个进口
  }
}
