package forex.services.oneforge

import java.time.OffsetDateTime

import akka.actor.ActorSystem
import akka.testkit.TestKit
import forex.domain.Currency._
import forex.domain.Rate
import forex.main.AppStack
import monix.execution.Scheduler
import org.atnos.eff.syntax.addon.monix.task.toTaskOps
import org.scalatest._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers.be
import EitherValues._
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

class HttpInterpreterTest() extends AnyFunSuite with BeforeAndAfterAll {

  private implicit val system: ActorSystem = ActorSystem("test_actor_system")

  private implicit val scheduler: Scheduler = monix.execution.Scheduler(system.dispatcher)

  override def afterAll(): Unit =
    TestKit.shutdownActorSystem(system)

  test("made a call should return right pair") {
    val requestedPair = Rate.Pair(USD, EUR)
    val appKey = "wVjjmBc9z1Zmephwk6uWqxw8alr8GfAv"
    val baseUrl = "https://api.1forge.com/quotes"

    val interpreter = Interpreters.http[AppStack](appKey, baseUrl)

    val effect = interpreter.get(requestedPair)

    val result = Await.result(effect.runAsync.runToFuture, 15 second)

    result.right.value.timestamp.value should be > OffsetDateTime.MIN
    result.right.value.pair should be(requestedPair)
    result.right.value.price.value should be > BigDecimal(0)

    interpreter.get(requestedPair)

    val result2 = Await.result(effect.runAsync.runToFuture, 15 second)

    result should be(result2)
  }

}
