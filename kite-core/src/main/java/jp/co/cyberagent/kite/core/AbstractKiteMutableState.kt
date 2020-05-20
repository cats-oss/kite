package jp.co.cyberagent.kite.core

import jp.co.cyberagent.kite.core.internal.subscriberManager

/**
 * Inherits this abstract class to create custom [KiteMutableState].
 * When [KiteMutableState.value] changed, calls [notifyChanged] to notify its subscriber.
 * When access the getter of [KiteMutableState.value], calls [subscribe] to add itself as
 * the dependency to any potential subscriber.
 */
abstract class AbstractKiteMutableState<T>(
  private val kiteContext: KiteContext
) : KiteMutableState<T> {

  protected fun subscribe() {
    kiteContext.subscriberManager.subscribeTo(this)
  }

  protected fun notifyChanged() {
    kiteContext.subscriberManager.notifyStateChanged(this)
  }
}
