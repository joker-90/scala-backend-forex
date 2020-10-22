package forex.main

import java.time.Instant

import forex.config._
import forex.processes.rates.converters.toProcessError
import forex.services.oneforge.client.OneForgeClient
import forex.services.oneforge.{ AppStackRunner, Environment, PairCacheWithDeadline }
import monix.eval.Task
import org.zalando.grafter.macros._

@readerOf[ApplicationConfig]
case class Runners(
    config: OneForgeConfig,
    actorSystems: ActorSystems
) {

  import actorSystems._

  implicit final lazy val environment: Environment = Environment(
    PairCacheWithDeadline.default(),
    OneForgeClient.http(config.apiKey, config.baseUrl),
    Instant.now
  )

  def runApp[R](app: AppEffect[R]): Task[R] =
    AppStackRunner
      .run(app)
      .flatMap(e ⇒ Task.fromEither(toProcessError _)(e))

}
