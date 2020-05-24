package jp.co.cyberagent.kite.runtime.internal

import java.util.concurrent.atomic.AtomicInteger

private data class KiteStateKey(val num: Int)

internal class KiteStateKeyGenerator {

  private val stateKeyGenerator = AtomicInteger(0)

  fun createStateKey(): Any {
    return KiteStateKey(stateKeyGenerator.getAndDecrement())
  }
}
