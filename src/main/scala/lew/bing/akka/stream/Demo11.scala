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

  implicit val system = ActorSystem("QuickStart")
  implicit val materializer = ActorMaterializer()
  implicit val order = ByteOrder.LITTLE_ENDIAN
  def main(args: Array[String]): Unit = {


    import scala.concurrent.ExecutionContext.Implicits.global
    //自定义graphstage
    val source = Source.fromGraph(new NumbersSource)
    source.via(new MyMapFlow(i => i - 102)).runWith(Sink.fromGraph(new StdoutSink))
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
    val log = materializer.system.log

    override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
      new GraphStageLogic(shape) {
        override def preStart(): Unit = pull(in)

        setHandler(in,new InHandler {
          override def onPush(): Unit = {
            log.info(s"next:${grab(in)}")
            pull(in)
          }

          override def onUpstreamFinish(): Unit = {
            super.onUpstreamFinish()
          }
        })
      }

    override val shape: SinkShape[Int] = SinkShape(in)
  }

  class MyMapFlow(function:(Int) => Int) extends GraphStage[FlowShape[Int,Int]] {

    val inlet = Inlet[Int]("map inlet")
    val outlet = Outlet[Int]("map outlet")
    val log = materializer.system.log
    override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
      new GraphStageLogic(shape) {
//        override def preStart(): Unit = pull(inlet)

        var nextNumber = 0
        setHandler(inlet,new InHandler {
          override def onPush(): Unit = {
            push(outlet,function(grab(inlet)))
          }
        })

        setHandler(outlet,new OutHandler {
          override def onPull(): Unit = {
            log.info("推送")

            pull(inlet)
          }
        })

      }

    override val shape: FlowShape[Int, Int] = FlowShape(inlet,outlet)
  }
}
