package forex.main

import java.time.Instant

import forex.config._
import forex.processes.rates.Service
import forex.services.oneforge.client.OneForgeClient
import forex.services.oneforge.{ Environment, PairCacheWithDeadline }
import org.zalando.grafter.macros._

@readerOf[ApplicationConfig]
case class EnvironmentReader(
    config: OneForgeConfig,
    actorSystems: ActorSystems
) {
  import actorSystems._

  implicit final lazy val environment: Environment = Environment(
    PairCacheWithDeadline.default(),
    OneForgeClient.http(config.apiKey, config.baseUrl),
    Instant.now
  )

  final val Rates = Service()

}
