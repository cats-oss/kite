package jp.co.cyberagent.kite.testing

import androidx.lifecycle.LifecycleOwner
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.StringSpec
import io.kotest.experimental.robolectric.RobolectricTest
import jp.co.cyberagent.kite.KiteDslScope
import jp.co.cyberagent.kite.KiteScopeModel
import jp.co.cyberagent.kite.requireByType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@RobolectricTest
class TestTestKiteDslScope : StringSpec({

  fun KiteDslScope.delay100Ms() {
    launch {
      delay(100)
    }
  }

  "Run kiteDsl should success" {
    shouldNotThrowAny {
      runTestKiteDsl {
        delay100Ms()
        requireByType<KiteScopeModel>()
        requireByType<LifecycleOwner>()
      }
    }
  }
})
