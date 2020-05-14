package jp.co.cyberagent.kite

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import jp.co.cyberagent.kite.internal.KiteStateSubscriberManager
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.reflect.KClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger

@KiteDslMaker
interface KiteDslScope {

  val lifecycleOwner: LifecycleOwner

  fun launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
  ): Job

  fun <T : Any> getService(kClass: KClass<T>): T
}

internal class KiteDslScopeImpl(
  override val lifecycleOwner: LifecycleOwner,
  internal val scopeModel: KiteComponentScopeModel
) : KiteDslScope {

  private val stateKeyGenerator = AtomicInteger(0)

  val stateDependencyManager =
    KiteStateSubscriberManager()

  fun createStateKey(): Any {
    return "KiteDslScopeImpl_StateKey_${stateKeyGenerator.incrementAndGet()}"
  }

  override fun launch(
    context: CoroutineContext,
    start: CoroutineStart,
    block: suspend CoroutineScope.() -> Unit
  ) = lifecycleOwner.lifecycleScope.launch(context, start, block)

  override fun <T : Any> getService(kClass: KClass<T>): T {
    return scopeModel.getService(kClass)
  }
}

inline fun <reified T : Any> KiteDslScope.getService(): T {
  return getService(T::class)
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
}
