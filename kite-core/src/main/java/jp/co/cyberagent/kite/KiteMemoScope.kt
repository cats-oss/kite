package jp.co.cyberagent.kite

import jp.co.cyberagent.kite.internal.KiteStateSubscriberManager
import jp.co.cyberagent.kite.internal.subscriberManager

@KiteDslMaker
object KiteMemoScope

private class KiteMemoState<T>(
  private val computation: () -> T,
  private val subscriberManager: KiteStateSubscriberManager
) : KiteGetter<T> {

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

fun <T> KiteDslScope.memoize(
  computation: KiteMemoScope.() -> T
): KiteGetter<T> {
  checkIsMainThread("memo")
  return KiteMemoState({ KiteMemoScope.run(computation) }, subscriberManager)
}
