package forex.interfaces.api.rates

import akka.http.scaladsl._
import forex.domain._

import scala.concurrent.Future

trait Directives {
  import Protocol._
  import server.Directives._
  import unmarshalling.Unmarshaller

  def getApiRequest: server.Directive1[GetApiRequest] =
    for {
      from ← parameter('from.as(currency))
      to ← parameter('to.as(currency))
    } yield GetApiRequest(from, to)

  private val currency: Unmarshaller[String, Currency] =
    Unmarshaller.apply[String, Currency](
      _ ⇒
        s ⇒
          Currency
            .fromString(s)
            .fold(Future.failed[Currency](new IllegalArgumentException("Currency not found!")))(c => Future.successful(c))
    )

}

object Directives extends Directives
