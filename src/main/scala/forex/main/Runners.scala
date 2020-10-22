package forex.main

import forex.config._
import forex.processes.rates.converters.toProcessError
import forex.services.oneforge.AppStackRunner
import monix.eval.Task
import org.zalando.grafter.macros._

@readerOf[ApplicationConfig]
case class Runners(processes: EnvironmentReader) {

  def runApp[R](
      app: AppEffect[R]
  ): Task[R] =
    AppStackRunner
      .run(app)(processes.environment)
      .flatMap(e ⇒ Task.fromEither(toProcessError _)(e))

}
