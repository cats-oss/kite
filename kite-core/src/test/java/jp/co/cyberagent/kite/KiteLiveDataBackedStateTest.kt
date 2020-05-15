package jp.co.cyberagent.kite

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.experimental.robolectric.RobolectricTest
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@RobolectricTest
class KiteLiveDataBackedStateTest : StringSpec({
  listener(ArchInstantTaskListener)

  val scopeModel by memoize { KiteScopeModel() }
  val kite by memoize {
    val owner = TestLifecycleOwner()
    KiteDslScopeImpl(owner, scopeModel)
  }

  "Create state in main thread should success" {
    shouldNotThrowAny { kite.state { 0 } }
  }

  "Create state in background thread should throw exception" {
    withContext(Dispatchers.IO) {
      shouldThrow<IllegalStateException> { kite.state { 0 } }
    }
  }

  "Update state value should success" {
    val state = kite.state { 0 }
    state.value = 1
    state.value shouldBe 1

    withContext(Dispatchers.IO) {
      state.value = 2
      state.value shouldBe 2
    }
  }
})
