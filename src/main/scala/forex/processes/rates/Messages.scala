package forex.processes.rates

import forex.domain._

import scala.util.control.NoStackTrace

object Messages {
  sealed trait Error extends Throwable with NoStackTrace
  object Error {
    final case class Generic(underlying: Throwable) extends Error
    final case class Service(underlying: Throwable) extends Error
  }

  final case class GetRequest(
      from: Currency,
      to: Currency
  ) {
    def toModel: Rate.Pair = Rate.Pair(from, to)
  }
}
