package actor.fetcher

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

class FetcherActor(db: ActorRef) extends Actor with ActorLogging {
  override def receive: Receive = {
    case o => log.info(s"unknown message $o")
  }
}

object FetcherActor {
  def props(db: ActorRef): Props = Props(classOf[FetcherActor], db)

  sealed trait FetcherMessage

  case class FetchArticle(guid: String, url: String) extends FetcherMessage

}
