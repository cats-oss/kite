package jp.co.cyberagent.kite

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
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

  fun <T : Any> addService(service: T, kClass: KClass<T>)

  fun <T : Any> getService(kClass: KClass<T>): T
}

inline fun <reified T : Any> KiteDslScope.addService(service: T) {
  addService(service, T::class)
}

inline fun <reified T : Any> KiteDslScope.getService(): T {
  return getService(T::class)
}

internal class KiteDslScopeImpl(
  override val lifecycleOwner: LifecycleOwner,
  internal val scopeModel: KiteComponentScopeModel
) : KiteDslScope {

  internal val stateDependencyManager = KiteStateSubscriberManager()

  override fun launch(
    context: CoroutineContext,
    start: CoroutineStart,
    block: suspend CoroutineScope.() -> Unit
  ) = lifecycleOwner.lifecycleScope.launch(context, start, block)

  override fun <T : Any> addService(service: T, kClass: KClass<T>) {
    scopeModel.addService(service, kClass)
  }

  override fun <T : Any> getService(kClass: KClass<T>): T {
    return scopeModel.getService(kClass)
  }
}

fun kiteDsl(
  lifecycleOwner: LifecycleOwner,
  scopeModel: KiteComponentScopeModel,
  body: KiteDslScope.() -> Unit
) {
  val currentState = lifecycleOwner.lifecycle.currentState
  check(currentState == Lifecycle.State.INITIALIZED) {
    "Only can invoke kiteDsl when lifecycle is at the INITIALIZED state. Current state is $currentState"
  }
  KiteDslScopeImpl(lifecycleOwner, scopeModel).apply(body)
  lifecycleOwner.lifecycle.addObserver(scopeModel.lifecycleObserver)
}
