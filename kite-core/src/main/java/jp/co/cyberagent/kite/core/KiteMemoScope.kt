package jp.co.cyberagent.kite.core

import jp.co.cyberagent.kite.core.internal.KiteStateSubscriberManager
import jp.co.cyberagent.kite.core.internal.RefOnlySubscriber
import jp.co.cyberagent.kite.core.internal.Subscriber
import jp.co.cyberagent.kite.core.internal.Unset

@KiteDslMaker
interface KiteMemoScope

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
  val subscriberManager = kiteContext.requireByType<KiteStateSubscriberManager>()
  return KiteMemoScopeImpl(subscriberManager).createState(computation)
}

private class KiteMemoScopeImpl(
  private val subscriberManager: KiteStateSubscriberManager
) : KiteMemoScope {

  fun <T> createState(computation: KiteMemoScope.() -> T): KiteState<T> {
    return KiteMemoState(subscriberManager) { this.computation() }
  }
}

private class KiteMemoState<T>(
  private val subscriberManager: KiteStateSubscriberManager,
  private val computation: () -> T
) : KiteState<T> {

  @Volatile
  private var _value: Any? = Unset
    set(value) {
      if (field != value) {
        field = value
        if (field !== Unset) {
          subscriberManager.notifyStateChanged(this)
        }
      }
    }

  init {
    subscriberManager.runAndSubscribe(
      Subscriber {
        val v = computation.invoke()
        subscriberManager.runAndSubscribe(
          RefOnlySubscriber {
            _value = v
          }
        )
      }
    )
  }

  override val value: T
    get() {
      subscriberManager.subscribeTo(this)
      @Suppress("UNCHECKED_CAST")
      return _value as T
    }
}
