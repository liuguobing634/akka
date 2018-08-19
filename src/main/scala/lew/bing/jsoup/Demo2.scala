package lew.bing.jsoup

import java.io.{BufferedReader, InputStreamReader}

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.Cookie
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer

/**
  * Created by 刘国兵 on 2017/8/26.
  */
object Demo2 {

  def main(args: Array[String]): Unit = {
    val cookie = """tvfe_boss_uuid=52dadc1849a663fd; eas_sid=M1E4c8N144s1s9D4R347F590V2; h_uid=H13261222352; RK=sVebdfK7ft; mobileUV=1_15c10ff3d8f_4ead; pac_uid=1_634335272; pgv_pvi=4861357056; UM_distinctid=15cdcffd5c6142b-08dfff0c17dbae-5393662-384000-15cdcffd5c7f30; _ga=GA1.2.561466322.1464083427; _gscu_661903259=79553202pdyeqp48; __v3_c_review_11558=0; __v3_c_last_11558=1501400844521; __v3_c_visitor=1501400523652130; pgv_pvid=1709706074; o_cookie=634335272; ptcz=76fe0064e7ad8bb4bd43fc5fcff2acc09d1349c17516ac2ebd3605ea595f7c4f; pt2gguin=o0634335272; PHPSESSID=thamnclafb1o9aqha76qcc9305; pgv_si=s4248892416; pubtoken=eXPIJgpo; wxuid=106027201; ywGuid=106027201; ywKey=yw1460399984; yuewen_template=Default; authcc=63a4%2Bdq%2FI2nVKKueE%2BIQyPLl%2F9EHILb4J%2B6oy%2BEdl3ZI2oFWddbuGp5Epgz%2FPTD1ytwtMDLEs1R9Kr6x%2BzXJ3oWzk5aU6JlzuVauiiHT7DXfhOugsSPE0vklCXNaDcuncCWQgBxmVryJULypdqw6pcKJdwsGNWpvCa%2FEyaNS88Wb6256LhGYOZlgVK%2B8L74soO%2F53WlFiovujs04"""
    implicit val system = ActorSystem("jsoup")
    implicit val ec = system.dispatcher
    implicit val materializer = ActorMaterializer()
    val http = Http(system)
    http.singleRequest(HttpRequest(HttpMethods.POST,
      "https://write.qq.com/booksubmit/chapteraddsubmit.html",
      List(Cookie(cookie.split(";").map(k => {
        val l = k.split("=")
        l(0).replace(" ","") -> l(1).replace(" ","")
      }):_*))
    , FormData("CBID" -> "22243545000134702",
        "type" -> "update",
        "CCID" -> "9735659581421613",
        "chaptertitle" -> "帮朋友做个广告，有兴趣的来看看",
        "content" -> "网游之镖局，27万字的书，已经肥了。\n　　笨太子......就是开创种田网游流，主角名王大富的那个。新书圣衣时代，15号开始上传，有他的FANS的可以去看看。",
        "_token" -> "eXPIJgpo",
        "_hash" -> "dae81debc1261a109c15d89681183198_fdad3d78436ff44f06f439fe27249dd7"
      ).toEntity(HttpCharsets.`UTF-8`)
    )).foreach({
      case HttpResponse(StatusCodes.OK, headers, entity, _) =>
        entity.dataBytes.fold(""){
          case (init, next) =>
            init + next.utf8String
        }.runForeach(k => {
          println(k)
        })
      case _ =>
        println("abc")
    })

    val s = new BufferedReader(new InputStreamReader(System.in)).readLine()
    //    Thread.sleep(1000)
    println(s)

    system.terminate()
  }

}
