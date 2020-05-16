package jp.co.cyberagent.kite.internal

import java.util.concurrent.atomic.AtomicInteger
import jp.co.cyberagent.kite.KiteDslScope
import jp.co.cyberagent.kite.setIfAbsent

internal class KiteStateKeyGenerator {

  private val stateKeyGenerator = AtomicInteger(0)

  fun createStateKey(): String {
    return "KiteStateKeyGenerator_${stateKeyGenerator.incrementAndGet()}"
  }
}

internal fun KiteDslScope.createStateKey(): String {
  val generator = setIfAbsent(KiteStateKeyGenerator::class) { KiteStateKeyGenerator() }
  return generator.createStateKey()
}
