package jp.co.cyberagent.kite

import jp.co.cyberagent.kite.internal.subscriberManager

fun KiteDslScope.subscribe(
  action: () -> Unit
) {
  subscriberManager.runAndResolveDependentState(Runnable(action))
}
