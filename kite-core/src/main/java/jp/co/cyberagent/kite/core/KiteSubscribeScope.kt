package jp.co.cyberagent.kite.core

import jp.co.cyberagent.kite.common.checkIsMainThread
import jp.co.cyberagent.kite.core.internal.subscriberManager

@KiteDslMaker
interface KiteSubscribeScope

private object KiteSubscribeImpl : KiteSubscribeScope

@Suppress("FunctionName")
private fun KiteSubscribeScope(): KiteSubscribeScope = KiteSubscribeImpl

fun KiteDslScope.subscribe(
  action: KiteSubscribeScope.() -> Unit
) {
  checkIsMainThread("subscribe")
  subscriberManager.runAndResolveDependentState(Runnable { KiteSubscribeScope().run(action) })
}
