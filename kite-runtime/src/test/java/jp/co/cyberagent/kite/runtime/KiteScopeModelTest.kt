package jp.co.cyberagent.kite.runtime

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.experimental.robolectric.RobolectricTest
import jp.co.cyberagent.kite.testcommon.memoize

@RobolectricTest
class KiteScopeModelTest : StringSpec({
  val scopeModel by memoize { KiteScopeModel() }

  "Add absent type service should success" {
    shouldNotThrowAny {
      scopeModel.addService(Int::class, 1)
      scopeModel.addService(String::class, "A")
    }
  }

  "Add existed type service should throw exception" {
    shouldThrow<IllegalStateException> {
      scopeModel.addService(Int::class, 1)
      scopeModel.addService(Int::class, 2)
    }
  }
})
