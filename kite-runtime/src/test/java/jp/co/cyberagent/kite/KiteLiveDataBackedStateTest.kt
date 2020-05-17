package jp.co.cyberagent.kite

import androidx.lifecycle.Lifecycle.State
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

  val lifecycleOwner by memoize { TestLifecycleOwner() }
  val scopeModelOwner by memoize { TestKiteScopeModelOwner() }
  val kite by memoize { kiteDsl(lifecycleOwner, scopeModelOwner) { /* no op */ } }

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
    val kiteCreator = { kiteDsl(lifecycleOwner, scopeModelOwner, null, {}) }
    forAll(
      row(
        kiteCreator().run {
          state { 3 } to state { "Kite" }
        }
      ),
      row(
        kiteCreator().run {
          state { 4 } to state { "Cat" }
        }
      ),
      row(
        kiteCreator().run {
          state { 5 } to state { "Dog" }
        }
      )
    ) { (state1, state2) ->
      state1.value shouldBe 3
      state2.value shouldBe "Kite"
    }
  }

  "Subscribe action should run when state changed with different value" {
    lifecycleOwner.lifecycle.currentState = State.RESUMED
    val state = kite.state { "Kite" }
    var invokeCnt = 0
    kite.subscribe {
      state.value
      invokeCnt++
    }
    invokeCnt shouldBe 1
    state.value = "Kite"
    invokeCnt shouldBe 1
    state.value = "Cat"
    invokeCnt shouldBe 2
  }

  "Subscribe action should only run when its state changed" {
    lifecycleOwner.lifecycle.currentState = State.RESUMED
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
