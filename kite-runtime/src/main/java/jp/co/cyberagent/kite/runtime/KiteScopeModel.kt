package jp.co.cyberagent.kite.runtime

import androidx.lifecycle.ViewModel
import jp.co.cyberagent.kite.core.KiteContext
import jp.co.cyberagent.kite.core.KiteDslScope
import jp.co.cyberagent.kite.core.KiteState
import kotlin.reflect.KClass

/**
 * Define a scope model for storing [KiteState].
 *
 * Any [KiteState] created inside [KiteDslScope] through [kiteDsl] will be stored by a [KiteScopeModel].
 * The scope model [KiteScopeModel] inherits the [ViewModel] so it can be created in association with a
 * lifecycle scope (an fragment or an activity). The scope model will be retained as long as the
 * lifecycle scope is alive. E.g. if it is an Activity, until it is finished.
 *
 * In other words, this means that the [KiteState] will not be destroyed if its owner is
 * destroyed for a configuration change (e.g. rotation). The new instance of the [KiteDslScope]
 * created by the owner will just re-connected to the existing [KiteScopeModel]. Then any referenced
 * [KiteState] will be restored from the [KiteScopeModel].
 */
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
