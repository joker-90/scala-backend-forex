package forex.services.oneforge

import forex.domain.Rate
import forex.services.oneforge.RatesStore.AppStack
import org.atnos.eff.Eff
import org.atnos.eff.addon.monix._
import org.atnos.eff.syntax.all._
import monix.eval._
import org.atnos.eff
import org.atnos.eff.syntax.addon.monix.task.toTaskOps


object PairStoreInterpreter {

  def run[R](value: Eff[AppStack, R])(implicit environment: Environment): Task[Either[Error, R]] = {
    value
      .runReader(environment)
      .runEither[Error]
      .runAsync
  }

}
