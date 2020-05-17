package jp.co.cyberagent.kite.internal

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.experimental.robolectric.RobolectricTest
import io.kotest.matchers.shouldBe
import jp.co.cyberagent.kite.KiteState
import jp.co.cyberagent.kite.TestKiteDslScope
import jp.co.cyberagent.kite.plusAssign
import jp.co.cyberagent.kite.requireByType
import jp.co.cyberagent.kite.testState
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
    kite.ctx += KiteStateSubscriberManager()
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
