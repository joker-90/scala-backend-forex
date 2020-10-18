package forex

package object processes {

  type Rates[F[_]] = rates.Processes[F]
  final val Rates = rates.Processes
  type RatesError = rates.Messages.Error
  final val RatesError = rates.Messages.Error

}
