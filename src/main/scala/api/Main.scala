package api

import actor.db.DbActor
import actor.db.DbActor.{Retrieve, Store}
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes.{Created, OK}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import models.DbEntryProtocol

import scala.concurrent.duration._

object Main extends App with DbEntryProtocol {

  implicit val system = ActorSystem("webserver-actor-system")
  implicit val materializer = ActorMaterializer()

  implicit val timeout = Timeout(2.seconds)

  val db = system.actorOf(DbActor.props, "db")

  val routes: Route =
    path("db") {
      get {
        parameters('key.as[String]) { key =>
          val value = db ? Retrieve(key)
          complete(value.mapTo[String])
        }
      } ~
        post {
          parameters('key.as[String], 'value.as[String]) { (key, value) =>
            db ! Store(key, value)
            complete(Created, s"$key stored.")
          }
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
