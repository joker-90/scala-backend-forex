package forex

package object processes {

  final val Rates = rates.Service
  type RatesError = rates.Messages.Error
  final val RatesError = rates.Messages.Error

}
