package forex.services.oneforge

import forex.services.oneforge.RatesStore.AppStack
import monix.eval._
import org.atnos.eff.Eff
import org.atnos.eff.syntax.addon.monix.task.toTaskOps
import org.atnos.eff.syntax.all._

object AppStackRunner {

  def run[R](value: Eff[AppStack, R])(implicit environment: Environment): Task[Either[Error, R]] =
    value
      .runReader(environment)
      .runEither[Error]
      .runAsync

}
