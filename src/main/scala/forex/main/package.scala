package forex

import cats.data.Reader
import forex.services.oneforge.Environment
import forex.services.oneforge.RatesStore.AppStack
import monix.eval.Task
import org.atnos.eff._
import org.zalando.grafter._

package object main {

  type AppEffect[R] = Eff[AppStack, R]

  def toStartErrorString(results: List[StartResult]): String =
    s"Application startup failed. Modules: ${results
      .collect {
        case StartError(message, ex) ⇒ s"$message [${ex.getMessage}]"
        case StartFailure(message)   ⇒ message
      }
      .mkString(", ")}"

  def toStartSuccessString(results: List[StartResult]): String =
    s"Application startup successful. Modules: ${results
      .collect {
        case StartOk(message) ⇒ message
      }
      .mkString(", ")}"

}
