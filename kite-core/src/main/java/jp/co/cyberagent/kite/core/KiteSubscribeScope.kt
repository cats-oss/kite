package jp.co.cyberagent.kite.core

import jp.co.cyberagent.kite.core.internal.KiteStateSubscriberManager
import jp.co.cyberagent.kite.core.internal.RefOnlySubscriber
import jp.co.cyberagent.kite.core.internal.Subscriber

/**
 * Define a scope for subscribing a action to the change of some [KiteState]s automatically.
 */
@KiteDslMaker
interface KiteSubscribeScope {

  /**
   * Any state referenced in the [block] will not be treated as a dependency.
   */
  fun <T> refOnly(block: KiteSubscribeScope.() -> T): T
}

/**
 * Run the [action] immediately and re run when one of its dependencies changed.
 * If the getter of any [KiteState] was referenced inside the [action] during the [action] running,
 * then that [KiteState] will become one dependency of the [action].
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
 * subscribe {
 *   // text will changed with 0, 1, 2, ..., 10
 *   textView.text = myState.value.toString()
 * }
 * ```
 */
fun KiteDslScope.subscribe(
  action: KiteSubscribeScope.() -> Unit
) {
  kiteContext.requireByType<MainThreadChecker>().checkIsMainThread("subscribe")
  val subscriberManager = kiteContext.requireByType<KiteStateSubscriberManager>()
  KiteSubscribeScopeImpl(subscriberManager).run(action)
}

private class KiteSubscribeScopeImpl(
  private val subscriberManager: KiteStateSubscriberManager
) : KiteSubscribeScope {

  fun run(action: KiteSubscribeScope.() -> Unit) {
    val runnable = Subscriber { this.action() }
    subscriberManager.runAndSubscribe(runnable)
  }

  override fun <T> refOnly(block: KiteSubscribeScope.() -> T): T {
    val runnable = RefOnlySubscriber { this.block() }
    return subscriberManager.runAndSubscribe(runnable)
  }
}
