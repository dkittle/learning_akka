package actor.rss

import java.net.URL

import actor.fetcher.FetcherActor.FetchArticle
import actor.rss.RssActor.ReadRss
import akka.actor.{Actor, ActorLogging, ActorRef, Props}

class RssActor(fetcher: ActorRef) extends Actor with ActorLogging {
  override def receive: Receive = {
    case ReadRss(url) =>
      val xml = scala.xml.XML.load(new URL(url))
      val items = xml \\ "item"
      items.foreach { item =>
        val link = (item \\ "link").text.trim
        fetcher ! FetchArticle((item \\ "guid").text, link)
      }
      sender() ! (xml \\ "channel" \ "title").text
    case o => log.info(s"unknown message $o")
  }
}

object RssActor {
  def props(fetcher: ActorRef): Props = Props(classOf[RssActor], fetcher)

  sealed trait RssMessage

  case class ReadRss(url: String) extends RssMessage

}
