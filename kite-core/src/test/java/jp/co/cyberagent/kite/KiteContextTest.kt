package jp.co.cyberagent.kite

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.experimental.robolectric.RobolectricTest
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe

@RobolectricTest
class KiteContextTest : StringSpec({
  val ctx by memoize { KiteContext() }

  "Set non exiting contextual value should success" {
    forAll(
      row("key", 1),
      row("kite", 2),
      row(3, "cat")
    ) { k, v ->
      shouldNotThrowAny {
        ctx[k] = v
      }
    }
  }

  "Set exiting contextual value should throw exception" {
    ctx["key"] = 1
    shouldThrow<IllegalStateException> {
      ctx["key"] = 2
      Unit
    }
  }

  "Set value then get with same key should return same value" {
    ctx["key"] = 1
    ctx.require<Int>("key") shouldBe 1
    ctx.get<Int>("key") shouldBe 1
  }

  "Get non existing value should should return null" {
    ctx.get<Int>("key") shouldBe null
  }

  "Get non existing value should should throw exception" {
    shouldThrow<IllegalArgumentException> {
      ctx.require<Int>("key")
    }
  }

  "Get same key with different type should throw exception" {
    shouldThrow<ClassCastException> {
      ctx["key"] = 1
      ctx.get<String>("key")
    }
  }

  "SetByType should use type as key" {
    ctx.setByType("Kite")
    ctx.requireByType<String>() shouldBe "Kite"
    ctx.getByType<String>() shouldBe "Kite"
    ctx.require<String>(String::class) shouldBe "Kite"
  }

  "SetIfAbsent multiple times with same key then get with same key should return old value" {
    ctx.setIfAbsent("key") { 1 } shouldBe 1
    ctx.setIfAbsent("key") { 2 } shouldBe 1
  }

  "Plus context should prefer latter context" {
    ctx["key1"] = 1
    ctx["commonKey"] = 2

    val extraCtx = KiteContext()
    extraCtx["key2"] = 3
    extraCtx["commonKey"] = 4

    val newCtx = ctx + extraCtx
    newCtx.keys shouldContainExactly listOf("key1", "key2", "commonKey")
    newCtx.get<Int>("key1") shouldBe 1
    newCtx.get<Int>("key2") shouldBe 3
    newCtx.get<Int>("commonKey") shouldBe 4
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
        extraCtx = kiteContextOf(String::class to "Kite")
      ) {
        getByType<String>() shouldBe "Kite"
      }

      getByType<String>() shouldBe "Cat"
    }
  }
})
