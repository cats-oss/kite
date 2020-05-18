package jp.co.cyberagent.kite.runtime.internal

import java.util.concurrent.atomic.AtomicInteger
import jp.co.cyberagent.kite.core.KiteContext
import jp.co.cyberagent.kite.core.setIfAbsent

private data class KiteStateKey(val num: Int)

internal class KiteStateKeyGenerator {

  private val stateKeyGenerator = AtomicInteger(0)

  fun createStateKey(): Any {
    return KiteStateKey(stateKeyGenerator.getAndDecrement())
  }
}

internal fun KiteContext.createStateKey(): Any {
  val generator = setIfAbsent(KiteStateKeyGenerator::class) { KiteStateKeyGenerator() }
  return generator.createStateKey()
}
