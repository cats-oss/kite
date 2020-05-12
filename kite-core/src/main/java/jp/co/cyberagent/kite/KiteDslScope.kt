package jp.co.cyberagent.kite

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@KiteDslMaker
class KiteDslScope(
  val lifecycleOwner: LifecycleOwner,
  stateHolderLazy: Lazy<KiteComponentScopeModel>
) {

  val componentScopeModel: KiteComponentScopeModel by stateHolderLazy

  val stateDependencyManager = KiteStateDependencyManager()

  fun launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
  ) = lifecycleOwner.lifecycleScope.launch(context, start, block)

  inline fun <reified T : Any> addService(service: T) {
    componentScopeModel.addService(service, T::class)
  }

  inline fun <reified T : Any> getService() {
    componentScopeModel.getService(T::class)
  }
}

fun kiteDsl(
  lifecycleOwner: LifecycleOwner,
  stateHolderLazy: Lazy<KiteComponentScopeModel>,
  body: KiteDslScope.() -> Unit
) {
  KiteDslScope(lifecycleOwner, stateHolderLazy).apply {
    componentScopeModel.registerIn(this)
    onCreate { body.invoke(this) }
  }
}
