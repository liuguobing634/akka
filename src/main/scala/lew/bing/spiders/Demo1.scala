package lew.bing.spiders

import java.io.File
import java.nio.file.Paths

import akka.actor.{Actor, ActorLogging, ActorSelection, ActorSystem, Props, ReceiveTimeout}
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes, Uri}
import akka.http.scaladsl.{Http, HttpExt}
import akka.pattern.pipe
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{FileIO, Sink, Source}
import akka.util.ByteString

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import scala.concurrent.duration._
import scala.util.{Failure, Success}
import scala.util.matching.Regex
import akka.pattern.ask

/**
  * Created by 刘国兵 on 2017/4/28.
  */
object Demo1 {

  case class Chapter(no:Int,title:String,content:String)

  def main(args: Array[String]): Unit = {
    val system = ActorSystem("spider-demo1")
    implicit val executionContext = system.dispatcher
    val requestUrl = system.actorOf(Props[RequestUrl],"request")
    val parseActor = system.actorOf(Props[ParseActor],"parse")
    requestUrl ! Uri("http://www.biqudu.com/14_14721/")

//    system.scheduler.scheduleOnce(4.minutes){system.terminate()}
  }

  class RequestUrl extends Actor with ActorLogging {

    implicit val dispatcher: ExecutionContext= context.dispatcher

    implicit val system: ActorSystem = context.system
    implicit val materializer = ActorMaterializer()

    val parseActor: ActorSelection = context.actorSelection("../parse")

    var http:Option[HttpExt] = None


    override def preStart(): Unit = {
      http = Some(Http())
    }

    //当收到uri是不变化的，但如果收到了string会变化，
    override def receive: Receive = {
      case uri:Uri =>
        http.foreach(h => {
          val resp = h.singleRequest(HttpRequest(uri = uri))
          resp.pipeTo(self)
        })
      case response:HttpResponse if response._1 == StatusCodes.OK =>
        println("收到")
        val entity = response.entity
        val body = entity.dataBytes
        val result = body.map(_.decodeString("utf-8")).runFold("")((s1,s2) => s1 ++ s2)
        result.onComplete {
          case Success(msg) =>
            parseActor ! msg
          case Failure(e) =>
            log.info("有错误")
        }
      case response:HttpResponse if response._1 != StatusCodes.OK =>
        log.info("错误")

    }

  }

  class ParseActor extends Actor with ActorLogging {

    var chapters:Seq[(String,StringBuilder,String)] = Seq.empty
    implicit val materializer = ActorMaterializer()

    val requestUrl:ActorSelection = context.actorSelection("../request")
    var i = 0

    var size = 0
    implicit val ec:ExecutionContext = context.system.dispatcher
    //如果30秒未收到任何消息就写入到文件中
    context.setReceiveTimeout(30.seconds)

    override def receive: Receive = {
      case result:String =>
        //正则分析得到链接
        //matcher
        val pattern = "<a href=\"([^\"]+)\">([^<]+)</a>".r
        val links = (for(link  <- pattern.findAllMatchIn(result)) yield (link.group(1),link.group(2))).filter(_._1.startsWith("/14_14721/")).toList
        //求了一次size会清空。。。
//                context.become(getPage)
//        println(links.size)
        size = links.size
        links.zipWithIndex.foreach{
          case (link,index) =>
            chapters :+= (link._2 , StringBuilder.newBuilder,link._1)
            context.system.scheduler.scheduleOnce((index/10).seconds){
              requestUrl ! Uri(s"http://www.biqudu.com${link._1}")
            }
        }

        context.become(parse)

    }

    def parse:Receive = {
      case result:String =>
        i += 1
        log.info("No."+i+"")
        val titlePattern = "<title>([^<]+)_万古神帝_笔趣阁</title>".r
        val contentPattern ="""<div id="content"><script>readx\(\);</script>﻿(.+)</div>""".r
        val title = for (t <- titlePattern.findFirstMatchIn(result)) yield t.group(1)
        title.flatMap {t =>
          contentPattern.findFirstMatchIn(result).map {c =>
            (t,c.group(1).replace("&nbsp;"," ").replace("<br/>","\n"))
          }
        }.foreach {
          case (tit,content) =>
            //将tit分成两部分`
            chapters.find(_._1==tit).foreach(_._2 ++= content)
        }
      case ReceiveTimeout =>
        //检测是否有空，如果有，则重新请求
        if (i < size){
          chapters.filter(_._2.isEmpty).zipWithIndex.foreach {
            case (link,index) =>
              context.system.scheduler.scheduleOnce((index/10).seconds){
                requestUrl ! Uri(s"http://www.biqudu.com${link._3}")
              }
          }
        }else {
          Source[ByteString](chapters.filter(_._2.nonEmpty).map(c => c._1 ++ "\r\n" ++ c._2 ++ "\r\n")
            .map(ByteString(_,"utf-8")).toList)
            .runWith(FileIO.toPath(new File("万古神帝.txt").toPath))
            .onComplete(_ => context.system.terminate())
        }

    }
  }

}
