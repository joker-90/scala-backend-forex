package forex.processes.rates

import forex.services.oneforge.Error.OneForgeClientError

package object converters {
  import Messages._

  def toProcessError[T <: Throwable](t: T): Error = t match {
    case e: OneForgeClientError ⇒ Error.Service(e)
    case e: Error               ⇒ e
    case e                      ⇒ Error.Generic(e)
  }

}
