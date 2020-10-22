package forex.processes.rates

import forex.domain.Rate
import forex.processes.rates.Messages.GetRequest
import forex.services.oneforge.RatesStore
import forex.services.oneforge.RatesStore.AppStack
import org.atnos.eff.Eff

final case class Service() {

  def get(request: GetRequest): Eff[AppStack, Rate] = RatesStore.get(request.toModel)

}
