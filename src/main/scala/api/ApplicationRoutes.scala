package api

import actor.db.DbActor
import actor.db.DbActor.{RetrieveValue, StoreValue}
import akka.actor.{ActorRef, ActorSystem}
import akka.event.Logging
import akka.pattern.ask
import akka.http.scaladsl.model.StatusCodes.{Created, OK}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.{get, post}
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.stream.ActorMaterializer
import akka.util.Timeout
import models.DbEntryProtocol

import scala.concurrent.duration._

trait ApplicationRoutes extends DbEntryProtocol {

  implicit def system: ActorSystem

  lazy val log = Logging(system, classOf[ApplicationRoutes])

  implicit lazy val timeout = Timeout(2.seconds)

  def db: ActorRef

  lazy val applicationRoutes: Route =
    path("db") {
      get {
        parameters('key.as[String]) { key =>
          val value = db ? RetrieveValue(key)
          complete(value.mapTo[String])
        }
      } ~
        post {
          parameters('key.as[String], 'value.as[String]) { (key, value) =>
            db ! StoreValue(key, value)
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

}
