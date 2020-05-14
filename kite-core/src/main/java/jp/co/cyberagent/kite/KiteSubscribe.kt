package jp.co.cyberagent.kite

fun KiteDslScope.subscribe(
  action: () -> Unit
) {
  this as KiteDslScopeImpl
  stateDependencyManager.runAndResolveDependentState(Runnable(action))
}
