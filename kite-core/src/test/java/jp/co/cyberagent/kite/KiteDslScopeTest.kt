package jp.co.cyberagent.kite

import androidx.lifecycle.Lifecycle.State
import androidx.lifecycle.lifecycleScope
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.experimental.robolectric.RobolectricTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
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

  "Set contextual value with key then get with same key should return same value" {
    kiteDsl(owner, kiteScopeModel) {
      setContextualValueIfAbsent("value") { 4 } shouldBe 4
      getContextualValue<Int>("value") shouldBe 4
    }
  }

  "Set contextual value then get should return same value" {
    kiteDsl(owner, kiteScopeModel) {
      setContextualValueIfAbsent { "Kite" } shouldBe "Kite"
      getContextualValue<String>() shouldBe "Kite"
    }
  }

  "Set contextual value multiple times with same key " +
    "then get with same key should return old value" {
      kiteDsl(owner, kiteScopeModel) {
        setContextualValueIfAbsent("value") { 3 } shouldBe 3
        setContextualValueIfAbsent("value") { 4 } shouldBe 3
        getContextualValue<Int>("value") shouldBe 3
      }
    }

  "Set contextual value multiple times then get should return old value" {
    kiteDsl(owner, kiteScopeModel) {
      setContextualValueIfAbsent { "Kite" } shouldBe "Kite"
      setContextualValueIfAbsent { "Cats" } shouldBe "Kite"
      getContextualValue<String>() shouldBe "Kite"
    }
  }

  "Get no existing contextual value should throw exception" {
    kiteDsl(owner, kiteScopeModel) {
      shouldThrow<IllegalStateException> {
        getContextualValue<String>()
      }
      shouldThrow<IllegalStateException> {
        getContextualValue<Int>("value")
      }
    }
  }

  "Check properties should be same instance as parameters" {
    kiteDsl(owner, kiteScopeModel) {
      lifecycleOwner shouldBeSameInstanceAs owner
      kiteScopeModel shouldBeSameInstanceAs kiteScopeModel
      coroutineContext shouldBeSameInstanceAs owner.lifecycleScope.coroutineContext
    }
  }
})
