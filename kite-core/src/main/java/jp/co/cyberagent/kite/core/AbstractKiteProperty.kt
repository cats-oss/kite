package jp.co.cyberagent.kite.core

import jp.co.cyberagent.kite.core.internal.subscriberManager

abstract class AbstractKiteProperty<T>(
  private val ctx: KiteContext
) : KiteProperty<T> {

  protected fun subscribe() {
    ctx.subscriberManager.subscribeTo(this)
  }

  protected fun notifyChanged() {
    ctx.subscriberManager.notifyStateChanged(this)
  }
}
