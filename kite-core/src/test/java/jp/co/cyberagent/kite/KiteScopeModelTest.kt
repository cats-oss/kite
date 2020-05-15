package jp.co.cyberagent.kite

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.experimental.robolectric.RobolectricTest
import io.kotest.matchers.shouldBe

@RobolectricTest
class KiteScopeModelTest : StringSpec({
  val scopeMode by memoize { TestKiteScopeModel() }

  "Add absent type service should success" {
    shouldNotThrowAny {
      scopeMode.addTestService(1)
      scopeMode.addTestService("A")
    }
  }

  "Add existed type service should throw exception" {
    shouldThrow<IllegalStateException> {
      scopeMode.addTestService(1)
      scopeMode.addTestService(2)
    }
  }

  "Get existed type service should success" {
    shouldNotThrowAny {
      scopeMode.addTestService("Kite")
      scopeMode.getService<String>() shouldBe "Kite"
    }
  }

  "Get absent service should throw exception" {
    shouldThrow<IllegalStateException> {
      scopeMode.getService<String>()
    }
  }
})

private class TestKiteScopeModel : KiteScopeModel() {

  inline fun <reified T : Any> addTestService(service: T) = addService(service)
}
