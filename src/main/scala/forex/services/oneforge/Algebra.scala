package forex.services.oneforge

import java.time.{ Duration, Instant }

import cats.data._
import forex.domain._
import forex.services.oneforge.PairCacheWithDeadline.Value
import forex.services.oneforge.client.OneForgeClient
import monix.eval.Task
import org.atnos.eff.addon.monix.task.{ _task, fromTask }
import org.atnos.eff.all.{ fromEither, _ }
import org.atnos.eff.{ |=, ConcurrentHashMapCache, Eff, Fx }

object RatesStore {
  type _either[R] = Either[Error, *] |= R

  type _readerEnvironment[R] = Reader[Environment, *] |= R

  type AppStack = Fx.fx3[Reader[Environment, *], Either[Error, *], Task]

  def memoize[R: _readerEnvironment](rate: Rate, maxAge: Duration): Eff[R, Unit] =
    for {
      environment ← ask
    } yield environment.cache.put(rate.pair, Value(rate, environment.now().plus(maxAge)))

  def retrieve[R: _readerEnvironment: _either: _task](pair: Rate.Pair): Eff[R, Rate] =
    for {
      env ← ask
      task ← fromTask(env.client.get(pair))
      result ← fromEither(task)
    } yield result

  def getFromCache[R: _readerEnvironment](pair: Rate.Pair): Eff[R, Option[Rate]] =
    for {
      environment ← ask
    } yield environment.cache.get(pair).filter(_.deadline.isAfter(environment.now())).map(_.rate)

  def get(pair: Rate.Pair): Eff[AppStack, Rate] =
    for {
      cached ← getFromCache[AppStack](pair)
      result ← cached.map(right[AppStack, Error, Rate](_)).getOrElse(retrieve[AppStack](pair))
      _ ← if (cached.isEmpty) memoize[AppStack](result, Duration.ofMinutes(5)) else Eff.pure[AppStack, Unit](())
    } yield result
}

trait PairCacheWithDeadline {
  def get(pair: Rate.Pair): Option[Value]
  def put(pair: Rate.Pair, value: Value)
}

object PairCacheWithDeadline {
  final case class Value(rate: Rate, deadline: Instant)

  def default(): PairCacheWithDeadline =
    new PairCacheWithDeadline {

      private val _map = ConcurrentHashMapCache()

      override def get(pair: Rate.Pair): Option[Value] = _map.get(pair)

      override def put(pair: Rate.Pair, value: Value): Unit = _map.put(pair, value)
    }
}

final case class Environment(
    cache: PairCacheWithDeadline,
    client: OneForgeClient,
    now: () ⇒ Instant
)
