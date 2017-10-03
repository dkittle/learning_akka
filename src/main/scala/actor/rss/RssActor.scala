package actor.rss

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

class RssActor(db: ActorRef) extends Actor with ActorLogging {
  override def receive: Receive = ???
}

object RssActor {
  def props(db: ActorRef): Props = ???
}
