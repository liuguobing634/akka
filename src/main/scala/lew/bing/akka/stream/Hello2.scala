package lew.bing.akka.stream

/**
  * Created by 刘国兵 on 2017/4/12.
  */
import akka.actor.ActorSystem
import akka.stream._

object Hello2 {

  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem("reactive-tweets")
    implicit val materializer = ActorMaterializer()

    final case class Author(handle: String)

    final case class Hashtag(name: String)

    final case class Tweet(author: Author, timestamp: Long, body: String) {
      def hashtags: Set[Hashtag] =
        body.split(" ").collect { case t if t.startsWith("#") => Hashtag(t) }.toSet
    }

    val akkaTag = Hashtag("#akka")
  }

}
