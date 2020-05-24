package jp.co.cyberagent.kite.core

import jp.co.cyberagent.kite.core.internal.KiteStateSubscriberManager

/**
 * Inherits this abstract class to create custom [KiteMutableState].
 * When [KiteMutableState.value] changed, calls [notifyChanged] to notify its subscriber.
 * When access the getter of [KiteMutableState.value], calls [subscribe] to add itself as
 * the dependency to any potential subscriber.
 */
abstract class AbstractKiteMutableState<T> : KiteMutableState<T> {

  /**
   * [KiteStateSubscriberManager] is used internal only, inject it when calling [state].
   */
  internal lateinit var subscriberManager: KiteStateSubscriberManager

  protected fun subscribe() {
    subscriberManager.subscribeTo(this)
  }

  protected fun notifyChanged() {
    subscriberManager.notifyStateChanged(this)
  }
}
