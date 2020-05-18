package jp.co.cyberagent.kite.runtime

import androidx.lifecycle.ViewModel
import jp.co.cyberagent.kite.core.KiteContext
import kotlin.reflect.KClass

class KiteScopeModel : ViewModel() {

  private val tagMap = mutableMapOf<Any, Any>()

  private val serviceMap = mutableMapOf<KClass<*>, Any>()

  fun addService(kClass: KClass<*>, service: Any) {
    check(kClass !in serviceMap) {
      "Service $kClass already added."
    }
    serviceMap[kClass] = service
  }

  internal fun <T : Any> createTagIfAbsent(key: Any, creator: () -> T): T {
    val v = tagMap.getOrPut(key, creator)
    @Suppress("UNCHECKED_CAST")
    return v as T
  }

  internal fun addServiceToContext(kiteContext: KiteContext) {
    serviceMap.forEach { (k, v) -> kiteContext[k] = v }
  }
}
