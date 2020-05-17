package jp.co.cyberagent.kite

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope

@KiteDslMaker
interface KiteDslScope : CoroutineScope, KiteContext {

  val lifecycleOwner: LifecycleOwner

  val scopeModel: KiteScopeModel

  val ctx: KiteContext
}

private class KiteDslScopeImpl(
  override val lifecycleOwner: LifecycleOwner,
  override val scopeModel: KiteScopeModel,
  override val ctx: KiteContext
) : KiteDslScope,
  CoroutineScope by lifecycleOwner.lifecycleScope,
  KiteContext by ctx

@Suppress("FunctionName")
internal fun KiteDslScope(
  lifecycleOwner: LifecycleOwner,
  scopeModel: KiteScopeModel,
  ctx: KiteContext = KiteContext()
): KiteDslScope = KiteDslScopeImpl(lifecycleOwner, scopeModel, ctx)

fun kiteDsl(
  lifecycleOwner: LifecycleOwner,
  scopeModel: KiteScopeModel,
  ctx: KiteContext = KiteContext(),
  body: KiteDslScope.() -> Unit
) {
  val currentState = lifecycleOwner.lifecycle.currentState
  check(currentState == Lifecycle.State.INITIALIZED) {
    "Only can invoke kiteDsl when lifecycle is at the INITIALIZED state. " +
      "Current state is $currentState"
  }
  KiteDslScopeImpl(lifecycleOwner, scopeModel, ctx).apply(body)
}
