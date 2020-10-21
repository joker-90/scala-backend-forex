package forex.main

import java.time.Instant

import forex.config._
import forex.services.oneforge.client.OneForgeClient
import forex.services.oneforge.{Environment, PairCacheWithDeadline}
import forex.{processes => p, services => s}
import org.zalando.grafter.macros._

@readerOf[ApplicationConfig]
case class Processes(
    config: OneForgeConfig,
    actorSystems: ActorSystems
) {
  import actorSystems._

  implicit final lazy val environment: Environment = Environment(
    PairCacheWithDeadline.default(),
    OneForgeClient(config.apiKey, config.baseUrl),
    Instant.now
  )

  final val Rates = p.Rates[AppEffect]

}
