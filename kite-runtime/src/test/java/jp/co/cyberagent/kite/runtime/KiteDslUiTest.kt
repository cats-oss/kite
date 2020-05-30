package jp.co.cyberagent.kite.runtime

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle.State
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.SavedStateHandle
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
      kiteDsl(owner, TestKiteViewModelProvider()) { /* no op */ }
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
        kiteDsl(owner, TestKiteViewModelProvider()) { /* no op */ }
      }
    }
  }

  "Activity kiteDsl should initialize with correct KiteContext" {
    var invoked = false
    TestActivity.onCreateAction = { activity ->
      activity.kiteDsl {
        kiteContext.requireByType<Activity>() shouldBeSameInstanceAs activity
        kiteContext.requireByType<Context>() shouldBeSameInstanceAs activity
        kiteContext.requireByType<LifecycleOwner>() shouldBeSameInstanceAs activity
        kiteContext.requireByType<SavedStateHandle>()
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
        kiteContext.requireByType<Activity>() shouldBeSameInstanceAs fragment.requireActivity()
        kiteContext.requireByType<Context>() shouldBeSameInstanceAs fragment.requireContext()
        kiteContext.requireByType<LifecycleOwner>() shouldBeSameInstanceAs
          fragment.viewLifecycleOwner
        kiteContext.requireByType<Fragment>() shouldBeSameInstanceAs fragment
        kiteContext.requireByType<SavedStateHandle>()
        invoked = true
      }
    }
    launchFragmentInContainer<TestFragment>().moveToState(State.RESUMED)
    invoked shouldBe true
  }
})
