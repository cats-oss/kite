package jp.co.cyberagent.kite.core

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import jp.co.cyberagent.kite.testcommon.memoize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.min

class KiteSubscribeScopeTest : StringSpec({
  val kite by memoize { TestKiteDslScope() }

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

  "Subscribe action should not run when memo state changed with same value" {
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

  "State referenced in referOnly action reference should not treated as dependency" {
    val cnt1 = kite.state { 0 }
    val cnt2 = kite.state { 0 }
    var sum: Int = 0
    kite.subscribe {
      sum = cnt1.value + refOnly { cnt2.value }
    }
    sum shouldBe 0
    cnt1.value = 1
    sum shouldBe 1
    cnt2.value = 100
    sum shouldBe 1
    cnt1.value = 2
    sum shouldBe 102
  }
})
