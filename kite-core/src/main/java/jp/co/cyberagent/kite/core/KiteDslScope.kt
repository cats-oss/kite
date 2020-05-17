package jp.co.cyberagent.kite.core

import kotlinx.coroutines.CoroutineScope

@KiteDslMaker
interface KiteDslScope : CoroutineScope, KiteContext {
  val ctx: KiteContext
}

private class KiteDsScopeImpl(
  coroutineScope: CoroutineScope,
  override val ctx: KiteContext
) : KiteDslScope,
  CoroutineScope by coroutineScope,
  KiteContext by ctx

@Suppress("FunctionName")
fun KiteDslScope(
  coroutineScope: CoroutineScope,
  ctx: KiteContext
): KiteDslScope = KiteDsScopeImpl(coroutineScope, ctx)
