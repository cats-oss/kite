package jp.co.cyberagent.kite.internal

import java.util.concurrent.atomic.AtomicInteger
import jp.co.cyberagent.kite.KiteDslScope
import jp.co.cyberagent.kite.setIfAbsent

private data class KiteStateKey(val num: Int)

internal class KiteStateKeyGenerator {

  private val stateKeyGenerator = AtomicInteger(0)

  fun createStateKey(): Any {
    return KiteStateKey(stateKeyGenerator.getAndDecrement())
  }
}

internal fun KiteDslScope.createStateKey(): Any {
  val generator = setIfAbsent(KiteStateKeyGenerator::class) { KiteStateKeyGenerator() }
  return generator.createStateKey()
}
