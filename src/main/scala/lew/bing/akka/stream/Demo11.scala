package lew.bing.akka.stream

import java.nio.ByteOrder

import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl.{BidiFlow, Flow, GraphDSL, Sink, Source}
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}
import akka.util.ByteString

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Random


/**
  * Created by 刘国兵 on 2017/4/14.
  */
object Demo11 {

  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem("QuickStart")
    implicit val materializer = ActorMaterializer()
    implicit val order = ByteOrder.LITTLE_ENDIAN
    import scala.concurrent.ExecutionContext.Implicits.global
    //自定义graphstage
    val source = Source.fromGraph(new NumbersSource)
    source.runWith(Sink.fromGraph(new StdoutSink))
  }

  class NumbersSource extends GraphStage[SourceShape[Int]] {
    val out: Outlet[Int] = Outlet("NumbersSource")
    override val shape:SourceShape[Int] = SourceShape(out)

    override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = {
      new GraphStageLogic(shape) {
        private var counter = 1
        private val random = new Random()
        setHandler(out,new OutHandler {
          override def onPull(): Unit = {
            if (counter < 100){
              push(out,random.nextInt(100))
              counter += 1
            }else{
              push(out,random.nextInt(100))
              completeStage()
            }
          }
        })
      }
    }
  }

  class StdoutSink extends GraphStage[SinkShape[Int]] {
    val in:Inlet[Int] = Inlet("StdoutSink")

    override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
      new GraphStageLogic(shape) {
        override def preStart(): Unit = pull(in)

        setHandler(in,new InHandler {
          override def onPush(): Unit = {
            println(s"thread:${Thread.currentThread().getName},value:${grab(in)}")
            pull(in)
          }

          override def onUpstreamFinish(): Unit = {
            super.onUpstreamFinish()
          }
        })
      }

    override val shape: SinkShape[Int] = SinkShape(in)
  }
}
