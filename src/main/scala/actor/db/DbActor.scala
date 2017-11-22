package actor.db

import actor.db.DbActor.{RetrieveValue, StoreValue}
import akka.actor.{Actor, ActorLogging, Props}
import akka.actor.Actor.Receive

import scala.collection.mutable

class DbActor extends Actor with ActorLogging {
  val db = new mutable.HashMap[String, Any]

  override def receive: Receive = {
    case StoreValue(key, value) =>
      db.put(key, value)
    case RetrieveValue(key) =>
      sender() ! db.getOrElse(key, "")
    case o => log.info("unknown message ", o)
  }

}

object DbActor {
  val props: Props = Props[DbActor]

  sealed trait DbMessage
  case class RetrieveValue(key: String) extends DbMessage
  case class StoreValue(key: String, value: Any) extends DbMessage

}
