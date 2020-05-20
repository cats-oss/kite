package jp.co.cyberagent.kite.core

import kotlinx.coroutines.CoroutineScope

/**
 * Define a scope for kite DSL. All kite DSL (like [state], [memo], etc) is an extension
 * on [KiteDslScope]. [KiteDslScope] inherits [CoroutineScope] so you start coroutine via [launch],
 * and [async]. [KiteDslScope] also inherits [KiteContext] and usually the behavior of [KiteContext]
 * should delegate to the property [KiteDslScope.kiteContext].
 */
@KiteDslMaker
interface KiteDslScope : CoroutineScope, KiteContext {
  val kiteContext: KiteContext
}

/**
 * A simple implementation for [KiteDslScope] with using delegate pattern. A default
 * [KiteCoroutineDispatchers] instance will be set into the [KiteContext] if absent.
 */
private class KiteDsScopeImpl(
  coroutineScope: CoroutineScope,
  override val kiteContext: KiteContext
) : KiteDslScope,
  CoroutineScope by coroutineScope,
  KiteContext by kiteContext {

  init {
    setIfAbsent(KiteCoroutineDispatchers::class) { KiteCoroutineDispatchers() }
  }
}

/**
 * Create a simple [KiteDslScope] from [coroutineScope] and [kiteContext].
 *
 * It's not recommend directly use this method to write kite DSL. Instead, you can use
 * [Fragment.kiteDsl] or [ComponetntActivity.kiteDsl] in production code. And use [TestKiteDslScope]
 * or [runTestKiteDsl] in test code.
 */
@Suppress("FunctionName")
fun KiteDslScope(
  coroutineScope: CoroutineScope,
  kiteContext: KiteContext
): KiteDslScope = KiteDsScopeImpl(coroutineScope, kiteContext)
