package jp.co.cyberagent.kite.internal

import jp.co.cyberagent.kite.KiteDslScope
import jp.co.cyberagent.kite.setContextualValueIfAbsent
import java.util.concurrent.atomic.AtomicInteger

internal class KiteStateKeyGenerator {

  private val stateKeyGenerator = AtomicInteger(0)

  fun createStateKey(): String {
    return "KiteDslScopeImpl_StateKey_${stateKeyGenerator.incrementAndGet()}"
  }
}

internal fun KiteDslScope.createStateKey(): String {
  val generator = setContextualValueIfAbsent { KiteStateKeyGenerator() }
  return generator.createStateKey()
}
