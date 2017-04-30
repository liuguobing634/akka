package lew.bing.akka.actor.fsm


import akka.actor.{ActorRef, FSM}

import scala.concurrent.duration._


/**
  * Created by 刘国兵 on 2017/4/28.
  */
object Demo1 {

  object Events {
    final case class SetTarget(ref:ActorRef)
    final case class Queue(obj:Any)
    case object Flush

    final case class Batch(obj: scala.collection.immutable.Seq[Any])
  }

  object States {
    sealed trait State
    case object Idle extends State
    case object Active extends State

    sealed trait Data
    case object Uninitialzed extends Data
    final case class Todo(target:ActorRef,queue:scala.collection.immutable.Seq[Any]) extends Data
  }

  import Events._
  import States._

  class Buncher extends FSM[State,Data] {
    startWith(Idle,Uninitialzed)

    when(Idle) {
      case Event(SetTarget(ref),Uninitialzed) =>
        stay using Todo(ref,Vector.empty)
    }

    when(Active, stateTimeout = 1.second) {
      case Event(Flush | StateTimeout, t:Todo) =>
        goto(Idle) using t.copy(queue = Vector.empty)
    }

    initialize()

  }

  def main(args: Array[String]): Unit = {
    //
  }


}
