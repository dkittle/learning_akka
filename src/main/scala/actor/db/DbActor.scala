package actor.db

import actor.db.DbActor.{KeyFound, KeyNotFound, RetrieveValue, StoreValue}
import akka.actor.{Actor, ActorLogging, Props}

import scala.collection.mutable

class DbActor extends Actor with ActorLogging {
  val db = new mutable.HashMap[String, Any]

  override def receive: Receive = {
    case StoreValue(key, value) =>
      db.put(key, value)
    case RetrieveValue(key) =>
      db.get(key) match {
        case None => sender() ! KeyNotFound
        case Some(x) => sender() ! KeyFound(x)
      }
    case o => log.info("unknown message ", o)
  }

}

object DbActor {
  val props: Props = Props[DbActor]

  sealed trait DbCommand
  case class RetrieveValue(key: String) extends DbCommand
  case class StoreValue(key: String, value: Any) extends DbCommand

  sealed trait DbEvent
  case object KeyNotFound extends DbEvent
  case class KeyFound(value: Any) extends DbEvent
}
