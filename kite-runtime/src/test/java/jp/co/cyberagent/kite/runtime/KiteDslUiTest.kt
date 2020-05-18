package jp.co.cyberagent.kite.runtime

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle.State
import androidx.lifecycle.LifecycleOwner
import androidx.test.core.app.launchActivity
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.experimental.robolectric.RobolectricTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.exhaustive
import jp.co.cyberagent.kite.androidtestcommon.TestActivity
import jp.co.cyberagent.kite.androidtestcommon.TestFragment
import jp.co.cyberagent.kite.androidtestcommon.TestLifecycleOwner
import jp.co.cyberagent.kite.core.requireByType

@RobolectricTest
class KiteDslUiTest : StringSpec({

  "Initialize kiteDsl at INITIALIZED should success" {
    val owner = TestLifecycleOwner()
    owner.lifecycle.currentState = State.INITIALIZED
    shouldNotThrowAny {
      kiteDsl(owner, TestKiteScopeModelOwner()) { /* no op */ }
    }
  }

  "Initialize kiteDsl not at INITIALIZED should throw exception" {
    val owner = TestLifecycleOwner()
    val states = listOf(
      State.CREATED, State.STARTED, State.RESUMED, State.DESTROYED
    ).exhaustive()
    checkAll(states) { s ->
      owner.lifecycle.currentState = s
      shouldThrow<IllegalStateException> {
        kiteDsl(owner, TestKiteScopeModelOwner()) { /* no op */ }
      }
    }
  }

  "KiteDsl should add ScopeModel services to context" {
    val factory = KiteScopeModelFactory().apply {
      addService("Kite")
      addService(2048)
    }
    kiteDsl(TestLifecycleOwner(), TestKiteScopeModelOwner(), factory) {
      requireByType<String>() shouldBe "Kite"
      requireByType<Int>() shouldBe 2048
    }
  }

  "Activity kiteDsl should initialize with correct KiteContext" {
    var invoked = false
    TestActivity.onCreateAction = { activity ->
      activity.kiteDsl {
        requireByType<Activity>() shouldBeSameInstanceAs activity
        requireByType<Context>() shouldBeSameInstanceAs activity
        requireByType<LifecycleOwner>() shouldBeSameInstanceAs activity
        invoked = true
      }
    }
    launchActivity<TestActivity>().use {
      it.moveToState(State.RESUMED)
      invoked shouldBe true
    }
  }

  "Fragment kiteDsl should initialize with correct KiteContext" {
    var invoked = false
    TestFragment.onCreateAction = { fragment ->
      fragment.kiteDsl {
        requireByType<Activity>() shouldBeSameInstanceAs fragment.requireActivity()
        requireByType<Context>() shouldBeSameInstanceAs fragment.requireContext()
        requireByType<LifecycleOwner>() shouldBeSameInstanceAs fragment.viewLifecycleOwner
        requireByType<Fragment>() shouldBeSameInstanceAs fragment
        invoked = true
      }
    }
    launchFragmentInContainer<TestFragment>().moveToState(State.RESUMED)
    invoked shouldBe true
  }
})
