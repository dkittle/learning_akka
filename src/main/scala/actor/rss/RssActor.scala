package actor.rss

import java.net.URL

import actor.db.DbActor.Store
import actor.rss.RssActor.ReadRss
import akka.actor.{Actor, ActorLogging, ActorRef, Props}

class RssActor(db: ActorRef) extends Actor with ActorLogging {
  override def receive: Receive = {
    case ReadRss(url) =>
      val xml = scala.xml.XML.load(new URL(url))
      val items = xml \\ "item"
      items.foreach { item =>
        val link = (item \\ "link").text.trim
        val text = de.l3s.boilerpipe.extractors.ArticleExtractor.INSTANCE.getText(new java.net.URL(link))
        db ! Store((item \\ "guid").text, text.trim)
      }
      sender() ! (xml \\ "channel" \ "title").text
    case o => log.info(s"unknown message $o")
  }
}

object RssActor {
  def props(db: ActorRef): Props = Props(classOf[RssActor], db)

  sealed trait RssMessage
  case class ReadRss(url: String) extends RssMessage

}
