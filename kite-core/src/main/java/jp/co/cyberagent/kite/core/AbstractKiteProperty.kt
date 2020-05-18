package jp.co.cyberagent.kite.core

import jp.co.cyberagent.kite.core.internal.subscriberManager

abstract class AbstractKiteProperty<T>(
  private val kiteContext: KiteContext
) : KiteProperty<T> {

  protected fun subscribe() {
    kiteContext.subscriberManager.subscribeTo(this)
  }

  protected fun notifyChanged() {
    kiteContext.subscriberManager.notifyStateChanged(this)
  }
}
