package jp.co.cyberagent.kite

import androidx.lifecycle.ViewModel
import kotlin.reflect.KClass

open class KiteComponentScopeModel : ViewModel() {

  private val tagMap = mutableMapOf<Any, Any>()

  private val serviceMap = mutableMapOf<KClass<*>, Any>()

  fun <T : Any> addService(service: T, kClass: KClass<T>) {
    check(kClass !in serviceMap.keys) {
      "Service $kClass already added."
    }
    serviceMap[kClass] = service
  }

  fun <T : Any> getService(kClass: KClass<T>): T {
    checkNotNull(serviceMap[kClass]) {
      "Service $kClass not found, please ensure add it via addService."
    }
    @Suppress("UNCHECKED_CAST")
    return serviceMap[kClass] as T
  }

  internal fun <T : Any> createTagIfAbsent(key: Any, creator: () -> T): T {
    val v = tagMap.getOrPut(key, creator)
    @Suppress("UNCHECKED_CAST")
    return v as T
  }
}
