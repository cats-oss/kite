package jp.co.cyberagent.kite.core

import jp.co.cyberagent.kite.core.internal.KiteStateSubscriberManager
import jp.co.cyberagent.kite.core.internal.Unset

@KiteDslMaker
interface KiteMemoScope

private object KiteMemoScopeImpl : KiteMemoScope

@Suppress("FunctionName")
private fun KiteMemoScope(): KiteMemoScope = KiteMemoScopeImpl

private class KiteMemoState<T>(
  private val computation: () -> T,
  private val subscriberManager: KiteStateSubscriberManager
) : KiteState<T> {

  @Volatile
  private var _value: Any? = Unset
    set(value) {
      if (field != value) {
        field = value
        subscriberManager.notifyStateChanged(this)
      }
    }

  private val runnable = Runnable {
    _value = computation.invoke()
  }

  init {
    subscriberManager.runAndResolveDependentState(runnable)
  }

  override val value: T
    get() {
      subscriberManager.subscribeTo(this)
      @Suppress("UNCHECKED_CAST")
      return _value as T
    }
}

/**
 * Evaluate the [computation] immediately and re evaluate when one of its dependencies changed.
 * If the getter of any [KiteState] was referenced inside the [computation] during the
 * [computation] running, then that [KiteState] will become one dependency of the [computation].
 * The evaluation result will be cached into a [KiteState].
 *
 * ## Usage:
 *
 * ```
 * val myState = state { 0 }
 *
 * launch {
 *   for(i in 0..10) {
 *     delay(1000)
 *     myState.value++
 *   }
 * }
 *
 * // myMemo value will changed with 0, 2, 4, ..., 20
 * val myMemo = memo {
 *   myState.value * 2
 * }
 * ```
 */
fun <T> KiteDslScope.memo(
  computation: KiteMemoScope.() -> T
): KiteState<T> {
  kiteContext.requireByType<MainThreadChecker>().checkIsMainThread("memo")
  return KiteMemoState({ KiteMemoScope().run(computation) }, kiteContext.requireByType())
}
