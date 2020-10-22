package forex.main

import forex.config._
import forex.processes.rates.converters.toProcessError
import forex.services.oneforge.PairStoreInterpreter
import monix.eval.Task
import org.zalando.grafter.macros._

@readerOf[ApplicationConfig]
case class Runners(
    processes: EnvironmentReader
) {

  def runApp[R](
      app: AppEffect[R]
  ): Task[R] =
    PairStoreInterpreter
      .run(app)(processes.environment)
      .flatMap(e ⇒ Task.fromEither(toProcessError _)(e))

}
