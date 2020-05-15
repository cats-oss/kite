package jp.co.cyberagent.kite

import jp.co.cyberagent.kite.internal.KiteStateSubscriberManager
import jp.co.cyberagent.kite.internal.subscriberManager

private class KiteMemoState<T>(
  private val computation: () -> T,
  private val subscriberManager: KiteStateSubscriberManager
) : KiteGetter<T> {

  private var _value: Any? = Unset

  override val value: T
    get() {
      if (_value == Unset) {
        subscriberManager.runAndResolveDependentState(
          Runnable {
            _value = computation.invoke()
            subscriberManager.notifyStateChanged(this)
          }
        )
      }
      subscriberManager.subscribeTo(this)
      @Suppress("UNCHECKED_CAST")
      return _value as T
    }
}

fun <T> KiteDslScope.memo(
  computation: () -> T
): KiteGetter<T> {
  this as KiteDslScopeImpl
  return KiteMemoState(computation, subscriberManager)
}
