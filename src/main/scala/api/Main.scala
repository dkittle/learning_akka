package api

import actor.db.DbActor
import actor.db.DbActor.{Keys, Retrieve, Store}
import actor.rss.RssActor
import actor.rss.RssActor.ReadRss
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes.{Created, OK}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import models.{RssUrl, RssUrlProtocol}

import scala.concurrent.duration._

object Main extends App with RssUrlProtocol {

  implicit val system = ActorSystem("webserver-actor-system")
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  implicit val timeout = Timeout(20.seconds)

  val db = system.actorOf(DbActor.props, "db")
  val rss = system.actorOf(RssActor.props(db), "rss-reader")

  val routes: Route =
    path("contents" / "url") {
      (post & entity(as[RssUrl])) { url =>
        val result = (rss ? ReadRss(url.url)).mapTo[String]
        onSuccess(result) { e =>
          complete(s"channel '${e.trim}' fetched")
        }
      }
    } ~
      path("contents" / "guids") {
        get {
          val guids = db ? Keys
          onSuccess(guids) { e =>
            complete(OK, e.toString)
          }
        }
      } ~
      path("contents" / "guid" / Segment) { key =>
        get {
//          parameters('key.as[String]) { key =>
          val result = db ? Retrieve(key)
          complete(OK, result.mapTo[String])
        }
      } ~
      path("quit") {
        post {
          system.terminate()
          complete(OK, s"article database system exiting\n")
        }
      }

  val config = ConfigFactory.load()
  val bindingFuture =
    Http().bindAndHandle(routes, config.getString("http.interface"), config.getInt("http.port"))
}
