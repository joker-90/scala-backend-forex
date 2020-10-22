package forex.services.oneforge

import scala.util.control.NoStackTrace

sealed abstract class Error(message: String) extends Throwable(message) with NoStackTrace

object Error {
  final case class System(underlying: Throwable) extends Error(underlying.getMessage)
  final case object EmptyResponse extends Error("Unexpected empty response from upstream service arrived!")
  final case class ParsingError(message: String)
      extends Error(s"Something went wrong while parsing upstream response: ${message}")

  type OneForgeClientError = Error
}
