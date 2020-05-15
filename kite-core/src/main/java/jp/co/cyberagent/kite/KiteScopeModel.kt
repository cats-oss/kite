package jp.co.cyberagent.kite

import androidx.lifecycle.ViewModel
import kotlin.reflect.KClass

open class KiteScopeModel : ViewModel() {

  private val tagMap = mutableMapOf<Any, Any>()

  private val serviceMap = mutableMapOf<KClass<*>, Any>()

  fun <T : Any> addService(service: T, kClass: KClass<T>) {
    check(kClass !in serviceMap) {
      "Service $kClass already added."
    }
    serviceMap[kClass] = service
  }

  fun <T : Any> getService(kClass: KClass<T>): T {
    check(kClass in serviceMap) {
      "Service $kClass not found, please ensure added it via addService."
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
