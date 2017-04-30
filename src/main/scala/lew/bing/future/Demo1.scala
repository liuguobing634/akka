package lew.bing.future
import java.util.concurrent.Executors

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

import scala.concurrent.{Await, ExecutionContext, Future, Promise}
import akka.pattern.pipe
import akka.util.Timeout

import scala.concurrent.duration._
import scala.util.Random


/**
  * Created by 刘国兵 on 2017/4/27.
  */
object Demo1 {

  def main(args: Array[String]): Unit = {
//    pipeTest()
//    monadTest()
//    import scala.concurrent.ExecutionContext.Implicits.global
//    val f1 = Future{(1 to 100).sum}
//    val f2 = Future{(100 to 1000).sum}
//    val f3 = for {
//      v1 <- f1
//      v2 <- f2
//    } yield v1 + v2
//    val v3 = Await.result(f3,3.seconds)
//    println(v3)
//    sequenceTest()
//    traverseTest()
//    reduceTest()
//    myPromise2()
//    myPromise()
//    afterTest()
//    monadTest2()
    sumOfSeqSquare2()
  }

  def afterTest():Unit = {
    val system = ActorSystem("after-test")
    implicit val executionContext = system.dispatcher
    import akka.pattern.after
    val delayed = after(200.millis,using = system.scheduler)(Future.failed(new IllegalStateException("超时了")))
    val future = Future {Thread.sleep(1000);"foo"}
    val result = Future firstCompletedOf Seq(future,delayed)
//    implicit val timeout:Timeout = 2.seconds
    result.onComplete{c =>
      println(c)
      system.terminate()
    }
  }


  def myPromise2():Unit = {
    println(s"当前线程为:${Thread.currentThread().getName}")
    val promise = Promise[Int]
    val executionContext = scala.concurrent.ExecutionContext.global
    executionContext.execute{() => {
      println(s"当前线程为:${Thread.currentThread().getName}")
      val result = (1 to 10).sum
      promise.success(result)
    }
    }
    println("hello")
    val future = promise.future
    println(Await.result(future,3.seconds))
  }

  def myPromise(): Unit ={
    import scala.concurrent.ExecutionContext.Implicits.global
    val p1 = Promise[Int]
    val p2 = Promise[Int]
    p1.success(2)
    p2.failure(new Exception("an error"))
    val future1 = p1.future
    val future2 = p2.future.recover{
      case e:Exception => 0
    }
    println(Await.result(future1,3.seconds))
    println(Await.result(future2,3.seconds))
  }

  def monadTest():Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global

    val f1 = Future {
      "Hello " + "World"
    }

    val f2 = Future.successful(2)

    val f3 = f1.flatMap {x =>
      f2.map {y =>
        x.length * y
      }
    }
    f3 foreach println

    Thread.sleep(1000)

  }

  def monadTest2():Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global
    val f1 = Future {
      "Hello " + "World"
    }

    val f2 = Future.successful(2)
    val f3 = for {
      x <- f1
      y <- f2
    } yield x.length * y
    f3 foreach println

    Thread.sleep(1000)
  }

  def sequenceTest():Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global
    val random = Random
    val listOfFuture = List.fill(100)(Future{random.nextInt(100)})
    val futureList = Future.sequence(listOfFuture)
    val sum = futureList.map(_.sum)
    println(Await.result(sum,3.seconds))
  }

  def traverseTest():Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global
    val random = Random
    val futureList = Future.traverse((1 to 100).toList)(_ => Future(Random.nextInt(100)))
    val sum = futureList.map(_.sum)
    println(Await.result(sum,3.seconds))
  }

  def reduceTest():Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global
    val futures = for (i <- 1 to 1000) yield Future(i * 2)
    val futureSum = Future.reduceLeft(futures)(_ + _)
    println(Await.result(futureSum,3.seconds))
  }

  def sumOfSeqSquare():Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global
    val futureList = Future.traverse((1 to 100).toList)(n => Future(n * n))
    val sum = futureList.map(_.sum)
    println(Await.result(sum,3.seconds))
  }

  def sumOfSeqSquare2():Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global
    val futures = (1 to 100).map(n => Future(n * n))
    val sum = Future.reduceLeft(futures)(_ + _)
    println(Await.result(sum,3.seconds))
  }

  def pipeTest():Unit ={
    val system = ActorSystem("future-demo1")
    import scala.concurrent.ExecutionContext.Implicits.global
    val aActor = system.actorOf(Props[AActor],"a")
    val future = Future {
      "Hello " + "World"
    }
    //通过pipe将future的信息传递到actor中
    future.pipeTo(aActor).onComplete(_ => system.terminate())
  }

  def promise(): Unit ={
    val service = Executors.newCachedThreadPool()
    implicit val ec = ExecutionContext.fromExecutorService(service)
    val f = Promise.successful("foo")

    ec.shutdown()
  }

  class AActor extends Actor with ActorLogging {
    override def receive: Receive = {
      case message:String  =>
        log.info("receive message {}",message)
    }
  }

}
