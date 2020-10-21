package forex.services.oneforge

import java.time.Duration
import java.time.temporal.ChronoUnit._

import forex.domain._
import org.atnos.eff.{ |=, Eff }

trait Algebra[F[_]] {
  def get(pair: Rate.Pair): F[Error Either Rate]
}

sealed trait PairStore[F]

final case class Retrieve(pair: Rate.Pair) extends PairStore[Error Either Rate]
final case class GetFromCache(pair: Rate.Pair) extends PairStore[Option[Rate]]
final case class Memoize(rate: Rate, maxAge: Duration) extends PairStore[Unit]

object PairStore {
  type _pairStore[F] = PairStore |= F

  type fallibleEff[F] = Eff[F, Error Either Rate]

  def memoize[F: _pairStore](rate: Rate, maxAge: Duration): Eff[F, Unit] =
    Eff.send[PairStore, F, Unit](Memoize(rate, maxAge))

  def retrieve[F: _pairStore](pair: Rate.Pair): fallibleEff[F] =
    Eff.send[PairStore, F, Error Either Rate](Retrieve(pair))

  def getFromCache[F: _pairStore](pair: Rate.Pair): Eff[F, Option[Rate]] =
    Eff.send[PairStore, F, Option[Rate]](GetFromCache(pair))

  def get[F: _pairStore](pair: Rate.Pair): Eff[F, Error Either Rate] =
    for {
      cached ← getFromCache(pair)
      result ← cached.map(p ⇒ Eff.pure[F, Either[Error, Rate]](Right(p))).getOrElse(retrieve(pair))
      _ ← result match {
        case Right(value) if cached.isEmpty ⇒ memoize(value, Duration.of(5, MINUTES))
        case _                              ⇒ Eff.pure[F, Unit](())
      }
    } yield result
}
