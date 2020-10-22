package forex.services.oneforge.client

import java.time.Instant
import java.time.ZoneOffset._

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri.{ Path, Query }
import akka.http.scaladsl.model.{ HttpRequest, Uri }
import akka.http.scaladsl.unmarshalling.Unmarshal
import cats.data.NonEmptyList
import cats.implicits._
import forex.domain._
import forex.services.oneforge.Error
import forex.services.oneforge.Error.{ EmptyResponse, ParsingError, System }
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import monix.eval.Task

import scala.concurrent.{ ExecutionContextExecutor, Future }

trait OneForgeClient {
  def get(pair: Rate.Pair): Task[Either[Error, Rate]]
}
object OneForgeClient {
  def http(apiKey: String, baseUrl: String)(implicit system: ActorSystem): OneForgeClient =
    HttpOneForgeClient(apiKey, baseUrl)
}

final case class HttpOneForgeClient private[oneforge] (apiKey: String, baseUrl: String)(implicit system: ActorSystem)
    extends OneForgeClient {

  private implicit val executorContext: ExecutionContextExecutor = system.dispatcher

  def get(pair: Rate.Pair): Task[Either[Error, Rate]] = Task.deferFuture(request(pair))

  private def request(pair: Rate.Pair): Future[Either[Error, Rate]] = {

    import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._

    Http()
      .singleRequest(buildHttpRequest(pair))
      .filter(_.status.isSuccess)
      .flatMap(Unmarshal(_).to[List[OneForgeResponse]])
      .map(NonEmptyList.fromList(_).toRight(EmptyResponse))
      .map(responses ⇒ responses.flatMap(_.head.toModel))
      .recover {
        case error: Error ⇒ Left(error)
        case th           ⇒ Left(System(th))
      }
  }

  private def buildHttpRequest(pair: Rate.Pair) =
    HttpRequest(
      uri = Uri(baseUrl)
        .withPath(Path("/quotes"))
        .withQuery(Query(("pairs", s"${pair.from.show}/${pair.to.show}"), ("api_key", apiKey)))
    )

  private final case class OneForgeResponse(s: String, p: BigDecimal, b: BigDecimal, a: BigDecimal, t: Long) { self ⇒

    def toModel: Either[Error, Rate] =
      parsePair(self.s) map (Rate(_, Price(p), parseTimestamp(self.t)))

    private def parsePair(rawPair: String): Either[Error, Rate.Pair] =
      for {
        currencies ← NonEmptyList
          .fromList(rawPair.split("/").toList)
          .filter(_.size >= 2)
          .toRight(ParsingError(s"malformed pair $rawPair"))
        first ← parseCurrency(currencies.head)
        second ← parseCurrency(currencies.tail.head)
      } yield Rate.Pair(first, second)

    private def parseCurrency(raw: String): Either[ParsingError, Currency] = Currency
        .fromString(raw)
        .toRight(ParsingError(s"unknown currency ${raw}"))

    private def parseTimestamp(rawTimestamp: Long): Timestamp = {
      val instant = Instant.ofEpochMilli(rawTimestamp)
      Timestamp(instant.atOffset(UTC))
    }
  }

  private object OneForgeResponse {
    implicit val responseDecoder: Decoder[OneForgeResponse] = deriveDecoder[OneForgeResponse]
  }
}
