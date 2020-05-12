package jp.co.cyberagent.kite

import androidx.lifecycle.ViewModel
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

open class KiteComponentScopeModel : ViewModel() {

  private val map = mutableMapOf<Any, Any>()

  private val serviceLocator = mutableMapOf<KClass<*>, Any>()

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

  fun <T : Any> addService(service: T, kClass: KClass<T>) {
    serviceLocator[kClass] = service
  }

  fun <T : Any> getService(kClass: KClass<T>): T {
    checkNotNull(serviceLocator[kClass]) {
      "Service $kClass is not added yet"
    }
    @Suppress("UNCHECKED_CAST")
    return serviceLocator[kClass] as T
  }
}
