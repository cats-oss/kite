package jp.co.cyberagent.kite.core.internal

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import jp.co.cyberagent.kite.core.KiteState
import jp.co.cyberagent.kite.core.TestKiteDslScope
import jp.co.cyberagent.kite.core.TestMainThreadChecker
import jp.co.cyberagent.kite.core.buildKiteContext
import jp.co.cyberagent.kite.core.requireByType
import jp.co.cyberagent.kite.core.setByType
import jp.co.cyberagent.kite.core.state
import jp.co.cyberagent.kite.testcommon.memoize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class KiteStateSubscriberManagerTest : StringSpec({

  val subscriberManager by memoize { KiteStateSubscriberManager(TestMainThreadChecker()) }

  "Invoke runAndResolveDependentState in background thread should throw exception" {
    shouldThrow<IllegalStateException> {
      withContext(Dispatchers.IO) {
        subscriberManager.runAndSubscribe(Subscriber { })
      }
    }
  }

  "Invoke notifyStateChanged in background thread should throw exception" {
    shouldThrow<IllegalStateException> {
      withContext(Dispatchers.IO) {
        subscriberManager.notifyStateChanged(object : KiteState<Any> {
          override val value: Any = Any()
        })
      }
    }
  }

  "Resolve dependency should success" {
    val kite = TestKiteDslScope(
      buildKiteContext {
        setByType(subscriberManager)
      }
    )
    val state1 = kite.state { 0 }
    val state2 = kite.state { 0 }
    var invokedCnt = 0
    val subscriber = Subscriber {
      state1.value + state2.value
      invokedCnt++
    }

    val subscribeManager = kite.kiteContext.requireByType<KiteStateSubscriberManager>()
    subscribeManager.runAndSubscribe(subscriber)
    invokedCnt shouldBe 1
    subscribeManager.notifyStateChanged(state1)
    invokedCnt shouldBe 2
    subscribeManager.notifyStateChanged(state2)
    invokedCnt shouldBe 3
  }
})
