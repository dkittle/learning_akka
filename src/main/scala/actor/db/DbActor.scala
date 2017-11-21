package actor.db

//class DbActor extends Actor with ActorLogging {
//  val db = new mutable.HashMap[String, Any]
//
//  override def receive: Receive = {
//    case Store(k, v) => db.put(k, v)
//    case Retrieve(k) => sender() ! db.getOrElse(k, "")
//    case Remove(k) => db.remove(k)
//    case Keys =>
//      if (db.isEmpty)
//        sender() ! Set.empty[String]
//      else {
//        val r = db.keySet
//        log.info(r.mkString(","))
//        sender() ! r
//      }
//    case o => log.info("unknown message ", o)
//  }
//
//}
//
//object DbActor {
//  val props: Props = Props[DbActor]
//
//  sealed trait DbMessage
//
//  case class Retrieve(key: String) extends DbMessage
//
//  case class Remove(key: String) extends DbMessage
//
//  case class Store(key: String, value: Any) extends DbMessage
//
//  case object Keys extends DbMessage
//
//}
