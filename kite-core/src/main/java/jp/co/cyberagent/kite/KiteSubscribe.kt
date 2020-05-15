package jp.co.cyberagent.kite

import jp.co.cyberagent.kite.internal.subscriberManager

fun KiteDslScope.subscribe(
  action: () -> Unit
) {
  this as KiteDslScopeImpl
  subscriberManager.runAndResolveDependentState(Runnable(action))
}
