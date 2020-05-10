package jp.co.cyberagent.kite

import androidx.lifecycle.ViewModel
import java.util.concurrent.atomic.AtomicInteger

open class KiteComponentScopeModel : ViewModel() {

  private val map = mutableMapOf<Any, Any>()

  private val keyGenerator = AtomicInteger(0)

  fun <T : Any> keepDuringLifecycle(creator: () -> T): T {
    val k = keyGenerator.getAndIncrement()
    val v = map.getOrPut(k, creator)
    @Suppress("UNCHECKED_CAST")
    return v as T
  }

  fun registerIn(dslUiScope: KiteDslScope) {
    dslUiScope.onDestroy { keyGenerator.set(0) }
  }
}
