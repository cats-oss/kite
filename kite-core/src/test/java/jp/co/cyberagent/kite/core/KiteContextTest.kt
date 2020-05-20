package jp.co.cyberagent.kite.core

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import jp.co.cyberagent.kite.testcommon.memoize

class KiteContextTest : StringSpec({
  val kiteContext by memoize { KiteContext() }

  "Set non exiting contextual value should success" {
    forAll(
      row("key", 1),
      row("kite", 2),
      row(3, "cat")
    ) { k, v ->
      shouldNotThrowAny {
        kiteContext[k] = v
      }
    }
  }

  "Set exiting contextual value should throw exception" {
    kiteContext["key"] = 1
    shouldThrow<IllegalStateException> {
      kiteContext["key"] = 2
      Unit
    }
  }

  "Set value then get with same key should return same value" {
    kiteContext["key"] = 1
    kiteContext.require<Int>("key") shouldBe 1
    kiteContext.get<Int>("key") shouldBe 1
  }

  "Get non existing value should should return null" {
    kiteContext.get<Int>("key") shouldBe null
  }

  "Get non existing value should should throw exception" {
    shouldThrow<IllegalArgumentException> {
      kiteContext.require<Int>("key")
    }
  }

  "Get same key with different type should throw exception" {
    shouldThrow<ClassCastException> {
      kiteContext["key"] = 1
      kiteContext.get<String>("key")
    }
  }

  "SetByType should use type as key" {
    kiteContext.setByType("Kite")
    kiteContext.requireByType<String>() shouldBe "Kite"
    kiteContext.getByType<String>() shouldBe "Kite"
    kiteContext.require<String>(String::class) shouldBe "Kite"
  }

  "SetIfAbsent multiple times with same key then get with same key should return old value" {
    kiteContext.setIfAbsent("key") { 1 } shouldBe 1
    kiteContext.setIfAbsent("key") { 2 } shouldBe 1
  }

  "Plus context should prefer latter context" {
    kiteContext["key1"] = 1
    kiteContext["commonKey"] = 2

    val extraKiteContext = KiteContext()
    extraKiteContext["key2"] = 3
    extraKiteContext["commonKey"] = 4

    val newKiteContext = kiteContext + extraKiteContext
    newKiteContext.keys shouldContainExactly listOf("key1", "key2", "commonKey")
    newKiteContext.get<Int>("key1") shouldBe 1
    newKiteContext.get<Int>("key2") shouldBe 3
    newKiteContext.get<Int>("commonKey") shouldBe 4
  }

  "KiteContextOf should create correct context" {
    kiteContextOf(
      "key" to 1,
      String::class to "Kite"
    ) should {
      it.keys shouldContainExactlyInAnyOrder listOf("key", String::class)
      it.get<Int>("key") shouldBe 1
      it.getByType<String>() shouldBe "Kite"
    }
  }

  "WithKiteContext should create a merged context" {
    TestKiteDslScope().apply {
      setByType("Cat")
      getByType<String>() shouldBe "Cat"

      withKiteContext(
        context = kiteContextOf(String::class to "Kite")
      ) {
        getByType<String>() shouldBe "Kite"
      }

      getByType<String>() shouldBe "Cat"
    }
  }
})
