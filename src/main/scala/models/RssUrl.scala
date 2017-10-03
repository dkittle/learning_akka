package models

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

case class RssUrl(url: String)

trait RssUrlProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val urlFormat: RootJsonFormat[RssUrl] = jsonFormat1(RssUrl.apply)
}
