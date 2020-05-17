package jp.co.cyberagent.kite

import androidx.lifecycle.Lifecycle.State
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.experimental.robolectric.RobolectricTest
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.exhaustive

@RobolectricTest
class KiteDslScopeTest : StringSpec({

  val owner by memoize { TestLifecycleOwner() }
  val kiteScopeModel by memoize { KiteScopeModel() }

  "Initialize kiteDsl at INITIALIZED should success" {
    owner.lifecycle.currentState = State.INITIALIZED
    shouldNotThrowAny {
      kiteDsl(owner, kiteScopeModel) { /* no op */ }
    }
  }

  "Initialize kiteDsl not at INITIALIZED should throw exception" {
    val states = listOf(
      State.CREATED, State.STARTED, State.RESUMED, State.DESTROYED
    ).exhaustive()
    checkAll(states) { s ->
      owner.lifecycle.currentState = s
      shouldThrow<IllegalStateException> {
        kiteDsl(owner, kiteScopeModel) { /* no op */ }
      }
    }
  }
})
