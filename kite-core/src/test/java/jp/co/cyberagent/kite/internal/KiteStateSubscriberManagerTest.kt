package jp.co.cyberagent.kite.internal

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.experimental.robolectric.RobolectricTest
import io.kotest.matchers.shouldBe
import jp.co.cyberagent.kite.KiteDslScopeImpl
import jp.co.cyberagent.kite.KiteScopeModel
import jp.co.cyberagent.kite.KiteState
import jp.co.cyberagent.kite.TestLifecycleOwner
import jp.co.cyberagent.kite.getContextualValue
import jp.co.cyberagent.kite.state
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

  "Resolve dependency" {
    val kite = KiteDslScopeImpl(TestLifecycleOwner(), KiteScopeModel())
    val state1 = kite.state { 0 }
    val state2 = kite.state { 0 }
    var invokedCnt = 0
    val runnable = Runnable {
      state1.value + state2.value
      invokedCnt++
    }

    val subscribeManager = kite.getContextualValue<KiteStateSubscriberManager>()
    subscribeManager.runAndResolveDependentState(runnable)
    invokedCnt shouldBe 1
    subscribeManager.notifyStateChanged(state1)
    invokedCnt shouldBe 2
    subscribeManager.notifyStateChanged(state2)
    invokedCnt shouldBe 3
  }
})
