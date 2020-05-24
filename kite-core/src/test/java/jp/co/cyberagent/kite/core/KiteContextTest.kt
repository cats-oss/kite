package jp.co.cyberagent.kite.core

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe

class KiteContextTest : StringSpec({

  "Create empty KiteContext should success" {
    KiteContext().keys shouldBe emptySet()
    kiteContextOf().keys shouldBe emptySet()
  }

  "buildKiteContext should build a KiteContext with specified key value" {
    val kiteContext = buildKiteContext {
      set("key", 1)
      setByType("value")
    }
    kiteContext.keys shouldContainExactlyInAnyOrder setOf("key", String::class)
    kiteContext.get<Int>("key") shouldBe 1
    kiteContext.getByType<String>() shouldBe "value"
  }

  "Set exiting contextual value should throw exception" {
    buildKiteContext {
      set("key", 1)
      shouldThrow<IllegalStateException> {
        set("key", 2)
      }
    }
  }

  "Set value then get with same key should return same value" {
    val kiteContext = buildKiteContext {
      set("key", 1)
    }
    kiteContext.require<Int>("key") shouldBe 1
    kiteContext.get<Int>("key") shouldBe 1
  }

  "Get non existing value should should return null" {
    KiteContext().get<Int>("key") shouldBe null
    kiteContextOf().get<Int>("key") shouldBe null
  }

  "Get non existing value should should throw exception" {
    shouldThrow<IllegalArgumentException> {
      KiteContext().require<Int>("key")
      kiteContextOf().require<Int>("key")
    }
  }

  "Get same key with different type should throw exception" {
    shouldThrow<ClassCastException> {
      val kiteContext = buildKiteContext {
        set("key", 1)
      }
      kiteContext.get<String>("key")
    }
  }

  "SetByType should use type as key" {
    val kiteContext = buildKiteContext {
      setByType("Kite")
    }
    kiteContext.requireByType<String>() shouldBe "Kite"
    kiteContext.getByType<String>() shouldBe "Kite"
    kiteContext.require<String>(String::class) shouldBe "Kite"
  }

  "Plus another context should prefer another context value if both have the same key" {
    val kiteContext = buildKiteContext {
      set("key1", 1)
      set("commonKey", 2)
    }

    val anotherKiteContext = buildKiteContext {
      set("key2", 3)
      set("commonKey", 4)
    }

    val newKiteContext = kiteContext + anotherKiteContext
    newKiteContext.keys shouldContainExactlyInAnyOrder setOf("key1", "key2", "commonKey")
    newKiteContext.get<Int>("key1") shouldBe 1
    newKiteContext.get<Int>("key2") shouldBe 3
    newKiteContext.get<Int>("commonKey") shouldBe 4
  }

  "KiteContextOf should create correct context" {
    kiteContextOf(
      "key" to 1,
      String::class to "Kite"
    ) should {
      it.keys shouldContainExactlyInAnyOrder setOf("key", String::class)
      it.get<Int>("key") shouldBe 1
      it.getByType<String>() shouldBe "Kite"
    }
  }

  "WithKiteContext should create a merged context" {
    val kiteContext = buildKiteContext {
      setByType("Cat")
    }
    TestKiteDslScope(kiteContext).apply {
      this.kiteContext.getByType<String>() shouldBe "Cat"

      withKiteContext(
        context = kiteContextOf(String::class to "Kite")
      ) {
        this.kiteContext.getByType<String>() shouldBe "Kite"
      }

      this.kiteContext.getByType<String>() shouldBe "Cat"
    }
  }
})
