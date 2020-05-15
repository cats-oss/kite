package jp.co.cyberagent.kite

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.CoroutineScope

@KiteDslMaker
interface KiteDslScope : CoroutineScope {

  val lifecycleOwner: LifecycleOwner

  val scopeModel: KiteScopeModel

  fun <T : Any> setContextualValueIfAbsent(key: Any, creator: () -> T): T

  fun <T : Any> getContextualValue(key: Any): T
}

internal class KiteDslScopeImpl(
  override val lifecycleOwner: LifecycleOwner,
  override val scopeModel: KiteScopeModel
) : KiteDslScope,
  CoroutineScope by lifecycleOwner.lifecycleScope {

  private val contextualValueMap = ConcurrentHashMap<Any, Any>()

  override fun <T : Any> setContextualValueIfAbsent(key: Any, creator: () -> T): T {
    val v = contextualValueMap.getOrPut(key, creator)
    @Suppress("UNCHECKED_CAST")
    return v as T
  }

  override fun <T : Any> getContextualValue(key: Any): T {
    check(contextualValueMap.containsKey(key)) {
      "Contextual value $key not found, please ensure added it via setContextualValueIfAbsent."
    }
    val v = contextualValueMap[key]
    @Suppress("UNCHECKED_CAST")
    return v as T
  }
}

inline fun <reified T : Any> KiteDslScope.setContextualValueIfAbsent(
  noinline creator: () -> T
): T {
  return setContextualValueIfAbsent(T::class, creator)
}

inline fun <reified T : Any> KiteDslScope.getContextualValue(): T {
  return getContextualValue(T::class)
}

fun kiteDsl(
  lifecycleOwner: LifecycleOwner,
  scopeModel: KiteScopeModel,
  body: KiteDslScope.() -> Unit
) {
  val currentState = lifecycleOwner.lifecycle.currentState
  check(currentState == Lifecycle.State.INITIALIZED) {
    "Only can invoke kiteDsl when lifecycle is at the INITIALIZED state. " +
      "Current state is $currentState"
  }
  KiteDslScopeImpl(lifecycleOwner, scopeModel).apply(body)
}
