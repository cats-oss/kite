package jp.co.cyberagent.kite

import androidx.lifecycle.ViewModel
import kotlin.reflect.KClass

open class KiteScopeModel : ViewModel() {

  private val tagMap = mutableMapOf<Any, Any>()

  private val serviceMap = mutableMapOf<KClass<*>, Any>()

  fun <T : Any> getService(kClass: KClass<T>): T {
    check(kClass in serviceMap) {
      "Service $kClass not found, please ensure added it via addService."
    }
    @Suppress("UNCHECKED_CAST")
    return serviceMap[kClass] as T
  }

  inline fun <reified T : Any> getService(): T {
    return getService(T::class)
  }

  protected fun <T : Any> addService(kClass: KClass<T>, service: T) {
    check(kClass !in serviceMap) {
      "Service $kClass already added."
    }
    serviceMap[kClass] = service
  }

  protected inline fun <reified T : Any> addService(service: T) {
    addService(T::class, service)
  }

  internal fun <T : Any> createTagIfAbsent(key: Any, creator: () -> T): T {
    val v = tagMap.getOrPut(key, creator)
    @Suppress("UNCHECKED_CAST")
    return v as T
  }
}

inline fun <reified T : Any> KiteDslScope.getService(): T {
  return scopeModel.getService()
}
