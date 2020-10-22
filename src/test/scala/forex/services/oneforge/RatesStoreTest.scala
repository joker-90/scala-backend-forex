package forex.services.oneforge

import java.time.Instant
import java.time.ZoneOffset._
import java.time.temporal.ChronoUnit._

import forex.domain.Currency.{ EUR, USD }
import forex.domain.{ Price, Rate, Timestamp }
import forex.services.oneforge.PairCacheWithDeadline.Value
import forex.services.oneforge.client.OneForgeClient
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import org.scalatest.EitherValues._
import org.scalatest.OptionValues._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers.be
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

class RatesStoreTest extends AnyFunSuite {

  test("rate store get pair rate for first time should return rate and memoize") {
    val pair = Rate.Pair(USD, EUR)

    val expectedRate = Rate(
      pair,
      Price(BigDecimal(5.5)),
      Timestamp.now
    )

    val now = Instant.now()

    implicit val env: Environment = testEnvironment(Right(expectedRate), now)

    val task = AppStackRunner.run(RatesStore.get(pair))

    val result = Await.result(task.runToFuture, 5 second)

    result.right.value should be(expectedRate)

    env.cache.get(pair).value.rate should be(expectedRate)
  }

  test("rate store get pair rate for expired time should return rate and memoize new") {
    val pair = Rate.Pair(USD, EUR)

    val expectedRate = Rate(
      pair,
      Price(BigDecimal(5.5)),
      Timestamp.now
    )

    val now = Instant.now()

    implicit val env: Environment = testEnvironment(Right(expectedRate), now)

    val expiredRate = Rate(pair, Price(BigDecimal(2)), Timestamp(now.minus(20, MINUTES).atOffset(UTC)))

    env.cache.put(pair, Value(expiredRate, now.minus(15, MINUTES)))

    val task = AppStackRunner.run(RatesStore.get(pair))

    val result = Await.result(task.runToFuture, 5 second)

    result.right.value should be(expectedRate)

    env.cache.get(pair).value.rate should be(expectedRate)
  }

  test("rate store should return fail if client fail") {
    val pair = Rate.Pair(USD, EUR)

    val now = Instant.now()

    val expectedException = Error.System(new Exception("Unexpected client exception!"))

    implicit val env: Environment = testEnvironment(Left(expectedException), now)

    val task = AppStackRunner.run(RatesStore.get(pair))

    val result = Await.result(task.runToFuture, 5 second)

    result.left.value shouldBe expectedException

    env.cache.get(pair) shouldBe None
  }

  private def testEnvironment(clientResponse: Either[Error, Rate], fixedNow: Instant) =
    Environment(
      PairCacheWithDeadline.default(),
      new OneForgeClient() {
        override def get(pair: Rate.Pair): Task[Either[Error, Rate]] = Task.now(clientResponse)
      },
      () ⇒ fixedNow
    )
}
