package actor.db

import actor.db.DurableDbActor._
import akka.actor.{Actor, ActorLogging, Props}
import akka.persistence.{PersistentActor, SaveSnapshotFailure, SaveSnapshotSuccess, SnapshotOffer}

import scala.collection.mutable

class DurableDbActor extends PersistentActor with ActorLogging {
  var db = new mutable.HashMap[String, Any]

  override def receiveCommand: Receive = {
    case StoreValue(k, v) =>
      persist(ValueStored(k, v)) { event =>
        log.info(s"stored $k")
        applyEvent(event)
        checkForSnapshot()
      }
    case Retrieve(k) => sender() ! db.getOrElse(k, "")
    case RemoveValue(k) =>
      persist(ValueRemoved(k)) { event =>
        log.info(s"removed $k")
        applyEvent(event)
        checkForSnapshot()
      }
    case Keys =>
      if (db.isEmpty)
        sender() ! Set.empty[String]
      else {
        val r = db.keySet
        log.info(r.mkString(","))
        sender() ! r
      }
    case SaveSnapshotSuccess(metadata) => log.info(metadata.toString)
    case SaveSnapshotFailure(metadata, reason) => log.error(s"Snapshot save failed $reason, ${metadata.toString}")
    case o => log.info("unknown message ", o)
  }

  private def checkForSnapshot() = {
    if (lastSequenceNr % 5 == 0) {
      log.info(s"saving snapshot")
      saveSnapshot(db)
    }
  }

  private def applyEvent(event: DbEvent): Unit = event match {
    case ValueStored(k, v) =>
      db.put(k, v)
    case ValueRemoved(k) =>
      db.remove(k)
  }

  override def receiveRecover: Receive = {
    case event: DbEvent => applyEvent(event)
    case SnapshotOffer(_, snapshot: mutable.HashMap[String, Any]) => {
      log.info(s"reloading state")
      db = snapshot
    }
  }

  override def persistenceId: String = self.path.toString
}

object DurableDbActor {
  val props: Props = Props[DurableDbActor]

  sealed trait DbCommand
  sealed trait DbEvent

  case class Retrieve(key: String) extends DbCommand

  case class RemoveValue(key: String) extends DbCommand

  case class StoreValue(key: String, value: Any) extends DbCommand

  case object Keys extends DbCommand

  case class ValueRemoved(key: String) extends DbEvent
  case class ValueStored(key: String, value: Any) extends DbEvent
}
