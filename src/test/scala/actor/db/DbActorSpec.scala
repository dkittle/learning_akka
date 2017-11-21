package actor.db

import akka.actor.{ActorSystem, Props}
import akka.testkit.{TestActorRef, TestKit}
import org.scalatest.{BeforeAndAfterAll, FlatSpec, FlatSpecLike, Matchers}

class DbActorSpec()
    extends TestKit(ActorSystem("test-system"))
    with FlatSpecLike
    with Matchers
    with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    super.afterAll()
    TestKit.shutdownActorSystem(system)
  }

//  "a db actor" should "store a value" in {
//    val actor = TestActorRef(new DbActor)
//    val db = actor.underlyingActor
//
//    actor ! ("a", "testing")
//    assert(db.db.contains("a"))
//  }
//
//  it should "not store a value if the key is not a string" in {
//    val actor = TestActorRef(new DbActor)
//    val db = actor.underlyingActor
//
//    actor ! (1, "testing")
//    assert(db.db.size === 0)
//  }
}
