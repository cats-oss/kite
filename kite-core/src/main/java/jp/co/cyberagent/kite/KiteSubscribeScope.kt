package jp.co.cyberagent.kite

import jp.co.cyberagent.kite.internal.subscriberManager

@KiteDslMaker
object KiteSubscribeScope

fun KiteDslScope.subscribe(
  action: KiteSubscribeScope.() -> Unit
) {
  checkIsMainThread("subscribe")
  subscriberManager.runAndResolveDependentState(Runnable { KiteSubscribeScope.run(action) })
}
