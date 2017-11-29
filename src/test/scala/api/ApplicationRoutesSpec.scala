package api

import actor.db.DbActor
import akka.actor.ActorRef
import akka.http.scaladsl.model.{ContentTypes, HttpRequest, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FlatSpec, Matchers}

class ApplicationRoutesSpec
    extends FlatSpec
    with Matchers
    with ScalaFutures
    with ScalatestRouteTest
    with ApplicationRoutes {

  lazy val routes: Route = applicationRoutes
  val db: ActorRef = system.actorOf(DbActor.props, "db")

  "a /get" should "return an empty result" in {
    val request = HttpRequest(uri = "/db?key=foo")
    request ~> routes ~> check {
      status should ===(StatusCodes.OK)
      contentType should ===(ContentTypes.`text/plain(UTF-8)`)
      entityAs[String] should ===("")
    }
  }
}
