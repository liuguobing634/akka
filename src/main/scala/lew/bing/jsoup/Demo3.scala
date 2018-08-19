package lew.bing.jsoup

import java.io.{BufferedReader, InputStreamReader}
import java.nio.file.{Files, Paths}

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.Cookie
import akka.http.scaladsl.{Http, HttpExt}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{FileIO, Flow, Source}
import akka.util.ByteString
import org.jsoup.Jsoup

import scala.collection.JavaConverters
import scala.concurrent.ExecutionContext
import spray.json._

/**
  * Created by 刘国兵 on 2017/8/26.
  */
object Demo3 {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("jsoup")
    implicit val ec = system.dispatcher
    implicit val materializer = ActorMaterializer()
    implicit val http = Http(system)
    val transver = Flow[String].map {str =>
      val jsoup = Jsoup.parse(str)
      val k = jsoup.select("[data-chapterid]")
      JavaConverters.asScalaIterator(k.iterator()).map {elem =>
        elem.attr("data-chapterid")
      }
    }

    val cookieStrings =  ("tvfe_boss_uuid=52dadc1849a663fd; " +
      "eas_sid=M1E4c8N144s1s9D4R347F590V2; " +
      "h_uid=H13261222352; RK=sVebdfK7ft; " +
      "mobileUV=1_15c10ff3d8f_4ead; " +
      "pac_uid=1_634335272; " +
      "pgv_pvi=4861357056; " +
      "UM_distinctid=15cdcffd5c6142b-08dfff0c17dbae-5393662-384000-15cdcffd5c7f30; " +
      "_ga=GA1.2.561466322.1464083427; _gscu_661903259=79553202pdyeqp48; " +
      "__v3_c_review_11558=0; __v3_c_last_11558=1501400844521; " +
      "__v3_c_visitor=1501400523652130; " +
      "pgv_pvid=1709706074; " +
      "o_cookie=634335272; " +
      "ptcz=76fe0064e7ad8bb4bd43fc5fcff2acc09d1349c17516ac2ebd3605ea595f7c4f; " +
      "pt2gguin=o0634335272; " +
      "PHPSESSID=thamnclafb1o9aqha76qcc9305; " +
      "pgv_si=s4248892416; " +
      "pubtoken=eXPIJgpo; " +
      "wxuid=106027201; " +
      "ywGuid=106027201; " +
      "ywKey=yw1460399984; yuewen_template=Default; " +
      "authcc=e92aK9Yis2p3ri7FhjoHrqNDJ7ecEFy3RoqbLPcbC1g1Dr1VKZ2CAm5Wpp3GKQNm3lsKK0v6xtP2Hc9fSB1rpBL83O8nz%2FUEkCSxKLP%2B%2B539D7%2FmdJJzD4tvBLUHtjIqVY3AFoobCO18iwzfTIUK7c5l2CDRpI3TWapgNFIty1SIgckpUk%2Buu%2FggMVssJJUK7ME5Ql6r7afk3eRQ")
        .split(";").map(l => {
        val k = l.split("=")
        k(0).trim -> k(1).trim
      })


    implicit val cookie=
      Cookie(cookieStrings:_*)
    val yueWenActor = system.actorOf(Props(new YueWenActor()))
    // 先获取所有的章节
    // 每传50个休息10s
    var i = 0
    http.singleRequest(HttpRequest(uri = "https://write.qq.com/booknovels/chaptermanage/CBID/22246229000277902.html",headers = List(cookie))).foreach{
      case HttpResponse(StatusCodes.OK,_,entity,_) =>
        entity.dataBytes.fold("")(_ + _.utf8String).via(transver).runForeach(_.foreach(
          {ccid =>
            i = i + 1
            if (i % 30 == 0) {
//              println("休息5s")
              Thread.sleep(5000)
            }
            yueWenActor ! (ccid,i)
          }
        ))
    }

    val s = new BufferedReader(new InputStreamReader(System.in)).readLine()
    //    Thread.sleep(1000)
    println(s)

    system.terminate()

  }

  // 创建一个actor，接受CCID，就查找并上传
  class YueWenActor(implicit http: HttpExt, cookie: Cookie, materializer: ActorMaterializer) extends Actor with ActorLogging {
    implicit val ec: ExecutionContext = context.system.dispatcher
    val logActor = context.actorOf(Props(new LogActor()))
    val __hash__ = "dae81debc1261a109c15d89681183198_fdad3d78436ff44f06f439fe27249dd7"
    val forbiddenStr = "k粉|白粉|冰毒|情色|龟头|fuck|sex|乱交|色色|婊子|凌辱|胯下|阴唇|小穴|强暴|精液|兽交|群交|叫床|援交|欲望|情欲|色欲|激情|调教|乱伦|私处|作爱|做爱|阴茎|阳具|阴道|阴蒂|肉棍|肉棒|肉洞|荡妇|阴囊|睾丸|射精|口交|屁眼|阴户|阴门|下体|阴毛|鸡巴|性欲|迷药|窃听|迷奸|春药|妓女|妓院|情妇|一夜情|二奶|罩杯|色狼|处女|床伴|青楼|内裤|底裤|内衣|亵衣|色魔|熟女|避孕套|波霸|初夜|蕾丝包养|上床|蹂躏|采花|偷欢|赤裸|猎艳|勾引|强奸|好色|淫荡|高潮|自慰|偷精|卖淫|性爱|性骚扰|意淫|抽插|破处|吹萧|打炮|失身|失禁|一夜|情人|小妞|玩偶|娇妻|流氓|情夫|小寡妇|老公们|禽兽|春色|春光|春梦|猛男|艳星|艳行|艳遇|艳记|艳事|艳史|寡妇|金瓶|禁果|小护士|浴女|外遇|变态|同居|诱惑|缠绵|暧昧|纵情|饥渴|出轨|呻吟|闷骚|风骚|堕落|坠落|沉沦|御用|调情|调戏|风流|不良|寻欢|合欢|偷情|偷腥|挑情|同眠|八九|绝经|视聊|操他|操她|玉图|艳影|艳图|艳漫|寻情|香纱|涩文|涩书|情涩|女涩|嫩嫩|辣图|绝蜜|禁猎|劲片|激爽|激辣|猛片|媚妙|妹力|骚妖|凸点|双峰|酥胸|臀沟|双沟|巨波|女优|娇点|裸戏|消魂|命根|嘿咻|黄片|云雨|禁区|咪咪|爱液|禁片|色图|AV|SM|胴体|裸体|三级|A级|A片|淫欲|绳虐|手淫|交媾|淫图|舔奶|肛门|贱货|失密|花逼|奸淫|日你|B样|我日|他妈|干你|你妈|贱比|贱逼|龟公|肛交|精子|性交|逼样|妈批|操你|大逼|操比|淫水|操逼|狂操|操蛋|傻逼|阴水|我操|傻B|鸡八|招妓|奶子|狗屎|烂比|包皮|烂逼|狗娘|骚货|发骚|猪操|狗操|狗B|群奸|卖比|二B|卖逼|二逼|反共|学运|台盟|伦功|台独|抡功|学潮|六四|法论|义解|天葬|天怒|蒙独|封杀|北韩|自焚|疆独|专制|毛片|法轮功|黑道|淫秽|超短|淫乱|裸照|欲火|褪去|巅峰|妖媚|持久|压在|长枪|娇躯|杀戮|挑逗|粉嫩|娇小|坚挺|嘴巴|颤抖|亲吻|游走|急促|爱抚|贝齿|抚摸|唇瓣|迷人|舌头|饱满|含住|屁股|湿吻|敏感|双腿|柔软|美艳|香艳|风情|臀部|纠缠|火热|黄色|肉体|娇吟|红唇|啪啪啪|美少女|坚硬|大腿|缠绕|火辣|风尘|弹性|美妙|性感|煽情|摩擦|把玩|狂野|诱人|白花花|玉体|高耸|水嫩|禁忌|插入|小嘴|丰满|杀器|巨龙|插进|三角|污秽|劲爆|沟壑|惊艳|禁卫|胸前|禄山之爪|肉球|胸脯|发抖|老二|伸出|龌龊|扫荡|露天|探索|呢喃|揉捏|黄龙|山峰|横行|小白兔|花瓣|推倒|撩拨|美娇娘|落红|贪婪|野外|赤果|口干舌燥|摸索|深入|酥麻|勾搭|抽动|垂涎|眉眼|羞愧|寂寞|美色|日日|起点|浸透|射了|长驱直入|鸣金收兵|波波|宠爱|娇羞|过瘾|浑圆|柔嫩|钢枪|快感|巨乳|轻吟|屠城|妩媚|鼓噪|枕边|伺候|小鸡鸡|蜜桃|绯红|潮红|福利|葡萄|精疲力竭|凸起|小兄弟|香舌|大屌"
    override def receive: Receive = {
      case (ccid: String, i: Int) =>
        // 两次请求
        // 先拿到数据

      http.singleRequest(HttpRequest(HttpMethods.POST,
        "https://write.qq.com/Contentv2/Booknovels/ajaxGetchapter.html",
        List(cookie),
        FormData(
          "CBID" -> "22246229000277902",
          "CCID" -> ccid,
          "_token" -> "eXPIJgpo"
        ).toEntity
      )).foreach{
        case HttpResponse(StatusCodes.OK,_,entity,_) =>
          entity.dataBytes.fold("")(_ + _.utf8String).map{str =>
            str.parseJson.asJsObject
          }.runForeach(obj => {
            val dataOpt = obj.fields.get("data")
            dataOpt.foreach(data => {
              val dataObj = data.asJsObject
              val dataMap = dataObj.fields
              if (dataMap.contains("content") && dataMap.contains("chaptertitle")) {

                val content = dataMap("content").asInstanceOf[JsString].value
                val title = dataMap("chaptertitle").asInstanceOf[JsString].value
                val replaceContent = content.replaceAll(forbiddenStr, "**")
                val replaceTitle = title.replaceAll(forbiddenStr,"**")
                if (replaceContent == content && replaceTitle == title) {
                  log.info(s"没有违禁词，不用管! $i")
                } else {
                  http.singleRequest(HttpRequest(HttpMethods.POST,
                    "https://write.qq.com/booksubmit/chapteraddsubmit.html",
                    List(cookie)
                    , FormData("CBID" -> "22246229000277902",
                      "type" -> "update",
                      "CCID" -> ccid,
                      "chaptertitle" -> replaceTitle,
                      "content" -> replaceContent,
                      "_token" -> "eXPIJgpo",
                      "_hash" -> __hash__
                    ).toEntity(HttpCharsets.`UTF-8`)
                  )).foreach {
                    case HttpResponse(StatusCodes.OK,_,entity2,_) =>
                      entity2.dataBytes.fold("")(_ + _.utf8String).map{str =>
                        str.parseJson.asJsObject
                      }.runForeach(obj2 => {
                        if (!obj2.fields("status").asInstanceOf[JsBoolean].value){
                          log.error(s"更新失败: $title")
                        }
                        log.info(s"完成了:$title")
                        println(s"完成了:$i")
                      })
                  }
                }
                // 上传到网站

              }
            })
          })
      }
        // 再修改内容
        // 上传
    }
  }

  class LogActor(implicit materializer: ActorMaterializer) extends Actor {
    implicit val ec: ExecutionContext = context.system.dispatcher
    override def receive: Receive = {
      case message: String =>
        Source(List(ByteString(message))).runWith(FileIO.toPath(Paths.get("message.log")))
    }
  }


}
