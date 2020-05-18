package jp.co.cyberagent.kite.core

import kotlinx.coroutines.CoroutineScope

@KiteDslMaker
interface KiteDslScope : CoroutineScope, KiteContext {
  val kiteContext: KiteContext
}

private class KiteDsScopeImpl(
  coroutineScope: CoroutineScope,
  override val kiteContext: KiteContext
) : KiteDslScope,
  CoroutineScope by coroutineScope,
  KiteContext by kiteContext

@Suppress("FunctionName")
fun KiteDslScope(
  coroutineScope: CoroutineScope,
  kiteContext: KiteContext
): KiteDslScope = KiteDsScopeImpl(coroutineScope, kiteContext)
