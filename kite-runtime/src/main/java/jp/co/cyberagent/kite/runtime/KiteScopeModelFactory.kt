package jp.co.cyberagent.kite.runtime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlin.reflect.KClass

open class KiteScopeModelFactory : ViewModelProvider.Factory {

  private val serviceMap = mutableMapOf<KClass<*>, Any>()

  fun <T : Any> addService(kClass: KClass<T>, service: T) {
    check(kClass !in serviceMap) {
      "Service $kClass already added."
    }
    serviceMap[kClass] = service
  }

  inline fun <reified T : Any> addService(service: T) {
    addService(T::class, service)
  }

  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    val scopeModel = KiteScopeModel()
    serviceMap.forEach { (k, v) ->
      scopeModel.addService(k, v)
    }
    @Suppress("UNCHECKED_CAST")
    return scopeModel as T
  }
}
