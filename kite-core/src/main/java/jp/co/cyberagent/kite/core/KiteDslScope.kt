package jp.co.cyberagent.kite.core

import jp.co.cyberagent.kite.core.internal.KiteStateSubscriberManager
import kotlinx.coroutines.CoroutineScope

/**
 * Define a scope for kite DSL. All kite DSL (like [state], [memo], etc) is an extension
 * on [KiteDslScope]. [KiteDslScope] implements [CoroutineScope] so you can start coroutine via
 * [launch], and [async].
 */
@KiteDslMaker
interface KiteDslScope : CoroutineScope {
  /**
   * The [KiteContext] of this scope.
   */
  val kiteContext: KiteContext
}

/**
 * A simple implementation for [KiteDslScope].
 */
private class KiteDsScopeImpl(
  coroutineScope: CoroutineScope,
  kiteContext: KiteContext
) : KiteDslScope,
  CoroutineScope by coroutineScope {

  override val kiteContext: KiteContext = buildKiteContext {
    setByType(KiteCoroutineDispatchers())
    val mainThreadChecker = kiteContext.requireByType<MainThreadChecker>()
    setByType(KiteStateSubscriberManager(mainThreadChecker))
  } + kiteContext
}

/**
 * Create a simple [KiteDslScope] from [coroutineScope] and [kiteContext]. A default
 * [KiteCoroutineDispatchers] instance will be set into the [kiteContext] if absent.
 *
 * It's not recommend to use the method to construct a [KiteDslScope] and write kite DSL.
 * Instead, you should use [Fragment.kiteDsl] or [ComponetntActivity.kiteDsl] in production code.
 * And use [TestKiteDslScope] or [runTestKiteDsl] in test code.
 */
@Suppress("FunctionName")
fun KiteDslScope(
  coroutineScope: CoroutineScope,
  kiteContext: KiteContext
): KiteDslScope = KiteDsScopeImpl(coroutineScope, kiteContext)
