package actor.db

import actor.db.DbActor.{KeyFound, KeyNotFound, RetrieveValue, StoreValue}
import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}

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

  it should "not return a value if no value has been stored" in {
    val actor = TestActorRef(new DbActor)
    actor ! RetrieveValue("a")
    expectMsg(timeout, KeyNotFound)
  }

  it should "return a value if it's been stored" in {
    val actor = TestActorRef(new DbActor)
    val db = actor.underlyingActor
    actor ! StoreValue("a", "testing")
    assert(db.db.contains("a"))

    actor ! RetrieveValue("a")
    expectMsg(timeout, KeyFound("testing"))
  }
}
