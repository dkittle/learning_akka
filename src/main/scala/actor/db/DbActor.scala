package actor.db

import actor.db.DbActor.{Retrieve, Store}
import akka.actor.{Actor, ActorLogging, Props}
import akka.actor.Actor.Receive

import scala.collection.mutable

class DbActor extends Actor with ActorLogging {
  val db = new mutable.HashMap[String, Any]

  override def receive: Receive = {
    case Store(k, v) => db.put(k, v)
    case Retrieve(k) => sender() ! db.getOrElse(k, "")
    case o => log.info("unknown message ", o)
  }

}

object DbActor {
  val props: Props = Props[DbActor]

  sealed trait DbMessage
  case class Retrieve(key: String) extends DbMessage
  case class Store(key: String, value: Any) extends DbMessage

}
