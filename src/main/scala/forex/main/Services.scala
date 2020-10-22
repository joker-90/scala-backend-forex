package forex.main

import java.time.Instant

import forex.config._
import forex.processes.rates.Service
import forex.services.oneforge.client.OneForgeClient
import forex.services.oneforge.{ Environment, PairCacheWithDeadline }
import org.zalando.grafter.macros._

@readerOf[ApplicationConfig]
case class Services(

) {


  final val Rates = Service()

}
