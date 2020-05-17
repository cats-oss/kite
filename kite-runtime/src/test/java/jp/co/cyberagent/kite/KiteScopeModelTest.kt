package jp.co.cyberagent.kite

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.experimental.robolectric.RobolectricTest

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
