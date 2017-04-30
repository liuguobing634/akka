package lew.bing.akka.actor.mailbox

import akka.actor.{ActorSystem, PoisonPill}
import akka.dispatch.{PriorityGenerator, UnboundedStablePriorityMailbox}
import com.typesafe.config.Config

/**
  * Created by 刘国兵 on 2017/4/27.
  */
class MyPrioMailbox(setting:ActorSystem.Settings,config:Config)
 extends UnboundedStablePriorityMailbox(
    PriorityGenerator {
      case 'highpriority => 0
      case 'lowpriority  => 2
      case PoisonPill     => 3
      case otherwise      => 1
    }


)


