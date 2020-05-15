package jp.co.cyberagent.kite.internal

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.experimental.robolectric.RobolectricTest
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import jp.co.cyberagent.kite.KiteDslScopeImpl
import jp.co.cyberagent.kite.KiteScopeModel
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
        subscriberManager.runAndResolveDependentState(mockk())
      }
    }
  }

  "Invoke notifyStateChanged in background thread should throw exception" {
    val subscriberManager = KiteStateSubscriberManager()
    shouldThrow<IllegalStateException> {
      withContext(Dispatchers.IO) {
        subscriberManager.notifyStateChanged(mockk())
      }
    }
  }

  "Resolve dependency" {
    val kite = KiteDslScopeImpl(TestLifecycleOwner(), KiteScopeModel())
    val state1 = kite.state { 0 }
    val state2 = kite.state { 0 }
    val runnable = spyk(Runnable { state1.value + state2.value })

    val subscribeManager = kite.getContextualValue<KiteStateSubscriberManager>()
    subscribeManager.runAndResolveDependentState(runnable)
    subscribeManager.notifyStateChanged(state1)
    subscribeManager.notifyStateChanged(state2)
    verify(exactly = 3) { runnable.run() }
  }
})
