package forex.services.oneforge.client

import java.time.OffsetDateTime

import akka.actor.ActorSystem
import akka.testkit.TestKit
import forex.domain.Currency._
import forex.domain.Rate
import monix.execution.Scheduler
import org.scalatest.BeforeAndAfterAll
import org.scalatest.EitherValues._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers.be
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

class OneForgeClientTest() extends AnyFunSuite with BeforeAndAfterAll {

  private implicit val system: ActorSystem = ActorSystem("test_actor_system")

  private implicit val scheduler: Scheduler = monix.execution.Scheduler(system.dispatcher)

  override def afterAll(): Unit =
    TestKit.shutdownActorSystem(system)

  test("made a call should return right pair rate") {
    val requestedPair = Rate.Pair(USD, EUR)
    val appKey = "wVjjmBc9z1Zmephwk6uWqxw8alr8GfAv"
    val baseUrl = "https://api.1forge.com/quotes"

    val client = OneForgeClient.http(appKey, baseUrl)

    val effect = client.get(requestedPair)

    val result = Await.result(effect.runToFuture, 15 second)

    result.right.value.timestamp.value should be > OffsetDateTime.MIN
    result.right.value.pair should be(requestedPair)
    result.right.value.price.value should be > BigDecimal(0)

    client.get(requestedPair)
  }

}
