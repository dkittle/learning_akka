package actor.fetcher

import actor.db.DurableDbActor.StoreValue
import actor.fetcher.FetcherActor.FetchArticle
import akka.actor.{Actor, ActorLogging, ActorRef, Props}

class FetcherActor(db: ActorRef) extends Actor with ActorLogging {
  override def receive: Receive = {
    case FetchArticle(guid, link) =>
      val text = de.l3s.boilerpipe.extractors.ArticleExtractor.INSTANCE.getText(new java.net.URL(link))
      log.info(s"downloaded article $guid")
      db ! StoreValue(guid, text.trim)
    case o => log.info(s"unknown message $o")
  }
}

object FetcherActor {
  def props(db: ActorRef): Props = Props(classOf[FetcherActor], db)

  sealed trait FetcherMessage

  case class FetchArticle(guid: String, url: String) extends FetcherMessage

}
