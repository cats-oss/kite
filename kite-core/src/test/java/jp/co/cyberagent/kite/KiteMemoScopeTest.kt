package jp.co.cyberagent.kite

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.experimental.robolectric.RobolectricTest
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@RobolectricTest
class KiteMemoScopeTest : StringSpec({
  val kite by memoize { TestKiteDslScope() }

  "Create memo in main thread should success" {
    shouldNotThrowAny { kite.memo { 0 } }
  }

  "Create memo in background thread should throw exception" {
    withContext(Dispatchers.IO) {
      shouldThrow<IllegalStateException> { kite.memo { 0 } }
    }
  }

  "Memo should compute immediately" {
    val state = kite.testState { 1 }
    val memo = kite.memo { state.value + 2 }
    memo.value shouldBe 3
  }

  "Memo should recompute when any dependent state changed" {
    val state1 = kite.testState { "A" }
    val state2 = kite.testState { 1 }
    val memo = kite.memo { state1.value + state2.value }
    memo.value shouldBe "A1"
    state1.value = "B"
    memo.value shouldBe "B1"
    state2.value = 2
    memo.value shouldBe "B2"
  }

  "Memo should recompute when nested dependent state changed" {
    val state = kite.testState { 1 }
    val memo1 = kite.memo { state.value }
    val memo2 = kite.memo { memo1.value }
    memo2.value shouldBe 1
    state.value = 2
    memo2.value shouldBe 2
    state.value = 3
    memo2.value shouldBe 3
  }
})
