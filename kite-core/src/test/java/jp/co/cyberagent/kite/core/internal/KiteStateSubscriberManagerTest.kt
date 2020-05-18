package jp.co.cyberagent.kite.core.internal

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.experimental.robolectric.RobolectricTest
import io.kotest.matchers.shouldBe
import jp.co.cyberagent.kite.core.KiteState
import jp.co.cyberagent.kite.core.TestKiteDslScope
import jp.co.cyberagent.kite.core.plusAssign
import jp.co.cyberagent.kite.core.requireByType
import jp.co.cyberagent.kite.core.testState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@RobolectricTest
class KiteStateSubscriberManagerTest : StringSpec({

  "Invoke runAndResolveDependentState in background thread should throw exception" {
    val subscriberManager = KiteStateSubscriberManager()
    shouldThrow<IllegalStateException> {
      withContext(Dispatchers.IO) {
        subscriberManager.runAndResolveDependentState(Runnable {})
      }
    }
  }

  "Invoke notifyStateChanged in background thread should throw exception" {
    val subscriberManager = KiteStateSubscriberManager()
    shouldThrow<IllegalStateException> {
      withContext(Dispatchers.IO) {
        subscriberManager.notifyStateChanged(object : KiteState {})
      }
    }
  }

  "Resolve dependency should success" {
    val kite = TestKiteDslScope()
    kite.kiteContext += KiteStateSubscriberManager()
    val state1 = kite.testState { 0 }
    val state2 = kite.testState { 0 }
    var invokedCnt = 0
    val runnable = Runnable {
      state1.value + state2.value
      invokedCnt++
    }

    val subscribeManager = kite.requireByType<KiteStateSubscriberManager>()
    subscribeManager.runAndResolveDependentState(runnable)
    invokedCnt shouldBe 1
    subscribeManager.notifyStateChanged(state1)
    invokedCnt shouldBe 2
    subscribeManager.notifyStateChanged(state2)
    invokedCnt shouldBe 3
  }
})
