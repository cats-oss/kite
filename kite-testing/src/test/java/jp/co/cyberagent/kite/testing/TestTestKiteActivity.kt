package jp.co.cyberagent.kite.testing

import android.app.Activity
import androidx.test.core.app.launchActivity
import io.kotest.core.spec.style.StringSpec
import io.kotest.experimental.robolectric.RobolectricTest
import io.kotest.matchers.shouldBe
import jp.co.cyberagent.kite.requireByType

@RobolectricTest
class TestTestKiteActivity : StringSpec({

  "Launch TestKiteActivity with layout and config should success" {
    var invoked = false
    val intent = TestKiteActivity.makeIntent(
      R.layout.layout_kite_test,
      TestKiteActivity.Config {
        requireByType<Activity>()
        invoked = true
      }
    )
    launchActivity<TestKiteActivity>(intent)
    invoked shouldBe true
  }
})
