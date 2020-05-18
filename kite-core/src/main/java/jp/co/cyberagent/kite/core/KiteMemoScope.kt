package jp.co.cyberagent.kite.core

import jp.co.cyberagent.kite.core.internal.KiteStateSubscriberManager
import jp.co.cyberagent.kite.core.internal.subscriberManager

@KiteDslMaker
interface KiteMemoScope

private object KiteMemoScopeImpl : KiteMemoScope

@Suppress("FunctionName")
private fun KiteMemoScope(): KiteMemoScope = KiteMemoScopeImpl

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

fun <T> KiteDslScope.memo(
  computation: KiteMemoScope.() -> T
): KiteGetter<T> {
  requireByType<MainThreadChecker>().checkIsMainThread("memo")
  return KiteMemoState({ KiteMemoScope().run(computation) }, subscriberManager)
}
