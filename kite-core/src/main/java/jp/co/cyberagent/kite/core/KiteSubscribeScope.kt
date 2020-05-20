package jp.co.cyberagent.kite.core

import jp.co.cyberagent.kite.core.internal.subscriberManager

@KiteDslMaker
interface KiteSubscribeScope

private object KiteSubscribeImpl : KiteSubscribeScope

@Suppress("FunctionName")
private fun KiteSubscribeScope(): KiteSubscribeScope = KiteSubscribeImpl

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
  requireByType<MainThreadChecker>().checkIsMainThread("subscribe")
  subscriberManager.runAndResolveDependentState(Runnable { KiteSubscribeScope().run(action) })
}
