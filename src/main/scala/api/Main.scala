package api

import actor.db.DbActor
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory

object Main extends App with ApplicationRoutes {

  implicit val system = ActorSystem("webserver-actor-system")
  implicit val materializer = ActorMaterializer()

  val db = system.actorOf(DbActor.props, "db")

  lazy val routes: Route = applicationRoutes

  val config = ConfigFactory.load()
  val bindingFuture =
    Http().bindAndHandle(routes, config.getString("http.interface"), config.getInt("http.port"))
}
