package lew.bing.zookeeper

import java.util.Date

import org.apache.zookeeper._

import scala.io.StdIn


/**
  * Created by 刘国兵 on 2017/9/24.
  */
object ZookeeperDemo1 {



  def main(args: Array[String]): Unit = {

    val client = new ZooKeeper("ub1:2181,ub2:2181,cent:2181", 30000, (event: WatchedEvent) => {
      println(event)
    })
    // 打印当前时间
    println(new Date())
    // 监测节点是否存在
    val stat = client.exists("/demo1", (event: WatchedEvent) => {
      println(event)
    })
    if (stat != null) {
      val data = client.getData("/demo1", new Watcher {
        override def process(event: WatchedEvent): Unit = {
          println(new Date())
        }
      }, null)
      println(new String(data, "utf-8"))
    } else {
      client.create("/demo1", "first".getBytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT)
    }

    Thread.sleep(1000)

//    client.create("/demo1", "hello".getBytes("utf-8"),ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT)

    client.setData("/demo1", "haha".getBytes,-1)
    StdIn.readLine()
    // 如果没有persistent，关闭客户端节点就消失不见
//    client.delete("/demo1", -1)
    client.close()

  }

}


