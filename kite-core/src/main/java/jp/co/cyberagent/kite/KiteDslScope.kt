package jp.co.cyberagent.kite

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.reflect.KClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@KiteDslMaker
interface KiteDslScope {

  val lifecycleOwner: LifecycleOwner

  fun launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
  ): Job

  fun <T> setContextualValueIfAbsent(key: Any, creator: () -> T): T

  fun <T> getContextualValue(key: Any): T

  fun <T : Any> getService(kClass: KClass<T>): T
}

internal class KiteDslScopeImpl(
  override val lifecycleOwner: LifecycleOwner,
  internal val scopeModel: KiteComponentScopeModel
) : KiteDslScope {

  private val contextualValueMap = ConcurrentHashMap<Any, Any>()

  override fun launch(
    context: CoroutineContext,
    start: CoroutineStart,
    block: suspend CoroutineScope.() -> Unit
  ) = lifecycleOwner.lifecycleScope.launch(context, start, block)

  override fun <T> setContextualValueIfAbsent(key: Any, creator: () -> T): T {
    val v = contextualValueMap.getOrPut(key, creator)
    @Suppress("UNCHECKED_CAST")
    return v as T
  }

  override fun <T> getContextualValue(key: Any): T {
    check(contextualValueMap.containsKey(key)) {
      "Contextual value $key not found, please ensure added it via setContextualValueIfAbsent."
    }
    val v = contextualValueMap[key]
    @Suppress("UNCHECKED_CAST")
    return v as T
  }

  override fun <T : Any> getService(kClass: KClass<T>): T {
    return scopeModel.getService(kClass)
  }
}

inline fun <reified T : Any> KiteDslScope.getService(): T {
  return getService(T::class)
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
  scopeModel: KiteComponentScopeModel,
  body: KiteDslScope.() -> Unit
) {
  val currentState = lifecycleOwner.lifecycle.currentState
  check(currentState == Lifecycle.State.INITIALIZED) {
    "Only can invoke kiteDsl when lifecycle is at the INITIALIZED state. " +
      "Current state is $currentState"
  }
  KiteDslScopeImpl(lifecycleOwner, scopeModel).apply(body)
}
