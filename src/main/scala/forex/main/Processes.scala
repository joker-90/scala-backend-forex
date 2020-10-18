package forex.main

import forex.config._
import forex.{ processes ⇒ p, services ⇒ s }
import org.zalando.grafter.macros._

@readerOf[ApplicationConfig]
case class Processes(
    config: OneForgeConfig,
    actorSystems: ActorSystems
) {
  import actorSystems._

  implicit final lazy val _oneForge: s.OneForge[AppEffect] = s.OneForge.http(config.apiKey, config.baseUrl)

  final val Rates = p.Rates[AppEffect]

}
