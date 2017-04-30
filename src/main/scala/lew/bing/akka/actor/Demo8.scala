package lew.bing.akka.actor

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

/**
  * Created by 刘国兵 on 2017/4/19.
  */
object Demo8 {

  def main(args: Array[String]): Unit = {

    val config = ConfigFactory.parseString(
      """
        my-dispatcher {
 |# Dispatcher is the name of the event-based dispatcher
 |type = Dispatcher
 |# What kind of ExecutionService to use
 |executor = "fork-join-executor"
 |# Configuration for the fork join pool
 |fork-join-executor {
 |# Min number of threads to cap factor-based parallelism number to
 |parallelism-min = 2
 |# Parallelism (threads) ... ceil(available processors * factor)
 |parallelism-factor = 2.0
 |# Max number of threads to cap factor-based parallelism number to
 |parallelism-max = 10
 |}
 |# Throughput defines the maximum number of messages to be
 |# processed per actor before the thread jumps to the next actor.
 |# Set to 1 for as fair as possible.
 |throughput = 100
 |}
 |
 |blocking-io-dispatcher {
 |type = Dispatcher
 |executor = "thread-pool-executor"
 |thread-pool-executor {
 |fixed-pool-size=32
 |}
 |throughput=1
 |}
      """.stripMargin)
    val system = ActorSystem("MyActor",config)
    implicit val executionContext = system.dispatchers.lookup("my-dispatcher")
    val myActor = system.actorOf(Props[MyActor].withDispatcher("my-dispatcher"),"myactor1")
  }

  class MyActor extends Actor with ActorLogging{
    override def receive: Receive = {
      case _ => log.info("收到消息")
    }
  }

}
