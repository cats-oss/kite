package jp.co.cyberagent.kite

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.experimental.robolectric.RobolectricTest
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@RobolectricTest
class KiteLiveDataBackedStateTest : StringSpec({
  addListener(ArchInstantTaskListener)

  val scopeModel by memoize { KiteScopeModel() }
  val owner by memoize { TestLifecycleOwner() }
  val kite by memoize { kiteDsl(owner, scopeModel) { /* no op */ } }

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

  "State should be reused when scope model unchanged" {
    forAll(
      row(
        kiteDsl(owner, scopeModel, {}).run {
          state { 3 } to state { "Kite" }
        }
      ),
      row(
        kiteDsl(owner, scopeModel, {}).run {
          state { 4 } to state { "Cat" }
        }
      ),
      row(
        kiteDsl(owner, scopeModel, {}).run {
          state { 5 } to state { "Dog" }
        }
      )
    ) { (state1, state2) ->
      state1.value shouldBe 3
      state2.value shouldBe "Kite"
    }
  }
})
