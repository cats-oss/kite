package jp.co.cyberagent.kite.timeline

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import jp.co.cyberagent.kite.core.state
import jp.co.cyberagent.kite.sample.R
import jp.co.cyberagent.kite.sample.counter.bindCounterExampleFragmentUi
import jp.co.cyberagent.kite.testing.TestKiteFragment
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [28])
class TestBindCounter {

  @Test
  fun testBindCounterToUi() {
    val factory = TestKiteFragment.makeFactory(
      R.layout.fragment_counter,
      TestKiteFragment.Config {
        val counter1 = state { 3 }
        val counter2 = state { 6 }
        bindCounterExampleFragmentUi(counter1, counter2)
      }
    )
    launchFragmentInContainer<TestKiteFragment>(
      themeResId = R.style.AppTheme,
      factory = factory
    ).moveToState(Lifecycle.State.RESUMED)
    Espresso.onView(ViewMatchers.withText("3"))
      .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    Espresso.onView(ViewMatchers.withText("6"))
      .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    Espresso.onView(ViewMatchers.withText("Sum: 9"))
      .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
  }
}
