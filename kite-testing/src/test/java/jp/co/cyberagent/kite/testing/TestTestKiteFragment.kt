package jp.co.cyberagent.kite.testing

import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.launchFragmentInContainer
import io.kotest.core.spec.style.StringSpec
import io.kotest.experimental.robolectric.RobolectricTest
import io.kotest.matchers.shouldBe
import jp.co.cyberagent.kite.core.requireByType

@RobolectricTest
class TestTestKiteFragment : StringSpec({

  "Launch TestKiteFragment with layout and config should success" {
    var invoked = false
    val factory = TestKiteFragment.makeFactory(
      R.layout.layout_kite_test,
      TestKiteFragment.Config {
        requireByType<Fragment>()
        invoked = true
      }
    )
    launchFragmentInContainer<TestKiteFragment>(factory = factory)
    invoked shouldBe true
  }
})
