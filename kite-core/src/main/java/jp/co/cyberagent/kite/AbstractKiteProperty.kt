package jp.co.cyberagent.kite

import jp.co.cyberagent.kite.internal.subscriberManager

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
