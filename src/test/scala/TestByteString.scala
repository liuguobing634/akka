import akka.util.ByteString
import lew.bing.akka.actor.Demo7.Storage.Get
import org.scalatest.{FlatSpec, Matchers}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.server._
import Directives._
import akka.http.scaladsl.model.HttpEntity

/**
  * Created by 刘国兵 on 2017/8/22.
  */
class TestByteString extends FlatSpec with Matchers with ScalatestRouteTest {

  val route = pathPrefix("test"){
    get {
      complete(HttpEntity("HaHa"))
    }
  }

  "s" should "ByteString(\"你好\").length==6" in {
    val s = ByteString("你好")
    s.length shouldBe 6
  }

  it should "Get(test) response HaHa" in {
    Get("/test") ~> route ~> check {
      responseAs[String] shouldEqual "HaHa"
    }
  }

  "regrex" should "\"我的家\" match \"家\"" in {
    assert("我的家".matches(".*家.*"))
  }



}
