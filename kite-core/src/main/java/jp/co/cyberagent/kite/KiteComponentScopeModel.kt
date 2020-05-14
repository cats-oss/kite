package jp.co.cyberagent.kite

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

open class KiteComponentScopeModel : ViewModel() {

  private val tagMap = mutableMapOf<Any, Any>()

  private val tagKeyGenerator = AtomicInteger(0)

  private val serviceMap = mutableMapOf<KClass<*>, Any>()

  val lifecycleObserver = object : DefaultLifecycleObserver {
    override fun onDestroy(owner: LifecycleOwner) {
      tagKeyGenerator.set(0)
    }
  }

  fun <T : Any> createTagIfAbsent(creator: () -> T): T {
    val k = tagKeyGenerator.getAndIncrement()
    val v = tagMap.getOrPut(k, creator)
    @Suppress("UNCHECKED_CAST")
    return v as T
  }

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
}
