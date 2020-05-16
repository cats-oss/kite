package jp.co.cyberagent.kite

import androidx.lifecycle.Lifecycle.State
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.experimental.robolectric.RobolectricTest
import io.kotest.matchers.shouldBe
import kotlin.math.min
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@RobolectricTest
class KiteSubscribeScopeTest : StringSpec({
  listener(ArchInstantTaskListener)

  val scopeModel by memoize { KiteScopeModel() }
  val owner by memoize { TestLifecycleOwner() }
  val kite by memoize { KiteDslScopeImpl(owner, scopeModel) }

  "Subscribe in main thread should success" {
    shouldNotThrowAny { kite.subscribe { /* no op */ } }
  }

  "Subscribe in background thread should throw exception" {
    withContext(Dispatchers.IO) {
      shouldThrow<IllegalStateException> { kite.subscribe { /* no op */ } }
    }
  }

  "Subscribe action should run immediately" {
    var invoked = false
    kite.subscribe { invoked = true }
    invoked shouldBe true
  }

  "Subscribe action should re run when any dependent state change" {
    owner.lifecycle.currentState = State.RESUMED
    val state1 = kite.state { "A" }
    val state2 = kite.state { 1 }
    var result: Any? = null
    kite.subscribe { result = state1.value + state2.value }
    result shouldBe "A1"
    state1.value = "B"
    result shouldBe "B1"
    state2.value = 2
    result shouldBe "B2"
  }

  "Subscribe action should re run when any dependent memo state change" {
    owner.lifecycle.currentState = State.RESUMED
    val state = kite.state { 1 }
    val memo = kite.memo { "A" + state.value }
    var result: Any? = null
    kite.subscribe { result = memo.value }
    result shouldBe "A1"
    state.value = 2
    result shouldBe "A2"
    state.value = 3
    result shouldBe "A3"
  }

  "Subscribe action should not run when state changed with same value" {
    owner.lifecycle.currentState = State.RESUMED
    val state = kite.state { "A" }
    var invokeCnt = 0
    kite.subscribe {
      state.value
      invokeCnt++
    }
    invokeCnt shouldBe 1
    state.value = "A"
    invokeCnt shouldBe 1
    state.value = "B"
    invokeCnt shouldBe 2
  }

  "Subscribe action should not run when memo state changed with same value" {
    owner.lifecycle.currentState = State.RESUMED
    val state = kite.state { 1 }
    val memo = kite.memo { min(state.value, 0) }
    var invokeCnt = 0
    kite.subscribe {
      memo.value
      invokeCnt++
    }
    invokeCnt shouldBe 1
    state.value = 2
    invokeCnt shouldBe 1
    state.value = -1
    invokeCnt shouldBe 2
  }

  "Subscribe action should only run when its dependent changed" {
    owner.lifecycle.currentState = State.RESUMED
    val state1 = kite.state { "" }
    val state2 = kite.state { "" }
    var invokeCnt1 = 0
    var invokeCnt2 = 0
    kite.subscribe {
      state1.value
      invokeCnt1++
    }
    kite.subscribe {
      state2.value
      invokeCnt2++
    }
    invokeCnt1 shouldBe 1
    invokeCnt2 shouldBe 1
    state1.value = "Kite"
    invokeCnt1 shouldBe 2
    invokeCnt2 shouldBe 1
    state2.value = "Kite"
    invokeCnt1 shouldBe 2
    invokeCnt2 shouldBe 2
  }
})
