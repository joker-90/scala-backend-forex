package forex.processes.rates

import cats.Monad
import cats.data.EitherT
import forex.domain._
import forex.services.oneforge.{Environment, PairStoreInterpreter}
import monix.eval.Task

final case class Processes[F[_]]() {
  import Messages._
  import converters._

  def get(request: GetRequest)(implicit M: Monad[F], environment: Environment): Task[Either[Error, Rate]] =
    EitherT(PairStoreInterpreter.run(request.toModel)).leftMap(toProcessError).value

}
