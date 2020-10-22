package forex.domain

import cats.Show
import io.circe._

sealed trait Currency
object Currency {
  final case object AUD extends Currency
  final case object CAD extends Currency
  final case object CHF extends Currency
  final case object EUR extends Currency
  final case object GBP extends Currency
  final case object NZD extends Currency
  final case object JPY extends Currency
  final case object SGD extends Currency
  final case object USD extends Currency

  implicit val show: Show[Currency] = Show.show {
    case AUD ⇒ "AUD"
    case CAD ⇒ "CAD"
    case CHF ⇒ "CHF"
    case EUR ⇒ "EUR"
    case GBP ⇒ "GBP"
    case NZD ⇒ "NZD"
    case JPY ⇒ "JPY"
    case SGD ⇒ "SGD"
    case USD ⇒ "USD"
  }

  def fromString(s: String): Option[Currency] = s match {
    case "AUD" | "aud" ⇒ Some(AUD)
    case "CAD" | "cad" ⇒ Some(CAD)
    case "CHF" | "chf" ⇒ Some(CHF)
    case "EUR" | "eur" ⇒ Some(EUR)
    case "GBP" | "gbp" ⇒ Some(GBP)
    case "NZD" | "nzd" ⇒ Some(NZD)
    case "JPY" | "jpy" ⇒ Some(JPY)
    case "SGD" | "sgd" ⇒ Some(SGD)
    case "USD" | "usd" ⇒ Some(USD)
    case _ ⇒ None
  }

  implicit val encoder: Encoder[Currency] =
    Encoder.instance[Currency] { show.show _ andThen Json.fromString }

}
