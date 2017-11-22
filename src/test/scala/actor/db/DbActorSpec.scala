package actor.db

import actor.db.DbActor.{RetrieveValue, StoreValue}
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import akka.util.Timeout
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}

import scala.concurrent.Future
import scala.concurrent.duration._

class DbActorSpec()
    extends TestKit(ActorSystem("test-system"))
    with FlatSpecLike
    with Matchers
    with BeforeAndAfterAll
    with ImplicitSender {

  implicit val timeout = 2.seconds

  override def afterAll(): Unit = {
    super.afterAll()
    TestKit.shutdownActorSystem(system)
  }

  "a db actor" should "store a value" in {
    val actor = TestActorRef(new DbActor)
    val db = actor.underlyingActor

    actor ! StoreValue("a", "testing")
    assert(db.db.contains("a"))
  }

  it should "not return a value if it's not been stored" in {
    val actor = TestActorRef(new DbActor)
    actor ! RetrieveValue("a")
    expectMsg(timeout, "")
  }

  it should "return a value if it's been stored" in {
    val actor = TestActorRef(new DbActor)
    val db = actor.underlyingActor
    actor ! StoreValue("a", "testing")
    assert(db.db.contains("a"))

    actor ! RetrieveValue("a")
    expectMsg(timeout, "testing")
  }
}
