package jp.co.cyberagent.kite

import androidx.lifecycle.ViewModel
import kotlin.reflect.KClass

private class SimpleKiteScopeViewModel(
  serviceMap: Map<KClass<*>, Any>
) : KiteScopeModel() {
  init {
    serviceMap.forEach { (k, v) ->
      @Suppress("UNCHECKED_CAST")
      addService(v, k as KClass<Any>)
    }
  }
}

class SimpleKiteScopeModelFactory : KiteScopeModelFactory {

  private val serviceMap = mutableMapOf<KClass<*>, Any>()

  fun <T : Any> addService(service: T, kClass: KClass<T>) {
    check(kClass !in serviceMap) {
      "Service $kClass already added."
    }
    serviceMap[kClass] = service
  }

  inline fun <reified T : Any> addService(service: T) {
    addService(service, T::class)
  }

  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    val scopeModel = SimpleKiteScopeViewModel(serviceMap)
    @Suppress("UNCHECKED_CAST")
    return scopeModel as T
  }
}
