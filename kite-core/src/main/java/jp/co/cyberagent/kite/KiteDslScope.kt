package jp.co.cyberagent.kite

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
  stateHolderLazy: Lazy<KiteComponentScopeModel>
) : KiteDslScope {

  internal val componentScopeModel: KiteComponentScopeModel by stateHolderLazy

  internal val stateDependencyManager = KiteStateDependencyManager()

  override fun launch(
    context: CoroutineContext,
    start: CoroutineStart,
    block: suspend CoroutineScope.() -> Unit
  ) = lifecycleOwner.lifecycleScope.launch(context, start, block)

  override fun <T : Any> addService(service: T, kClass: KClass<T>) {
    componentScopeModel.addService(service, kClass)
  }

  override fun <T : Any> getService(kClass: KClass<T>): T {
    return componentScopeModel.getService(kClass)
  }
}

fun kiteDsl(
  lifecycleOwner: LifecycleOwner,
  stateHolderLazy: Lazy<KiteComponentScopeModel>,
  body: KiteDslScope.() -> Unit
) {
  KiteDslScopeImpl(lifecycleOwner, stateHolderLazy).apply {
    onCreate { body.invoke(this) }
    onDestroy { componentScopeModel.keyGenerator.set(0) }
  }
}
