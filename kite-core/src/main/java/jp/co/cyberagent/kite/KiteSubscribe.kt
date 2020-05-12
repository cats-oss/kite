package jp.co.cyberagent.kite

fun KiteDslScope.subscribe(
  action: () -> Unit
) {
  this as KiteDslScopeImpl
  stateDependencyManager.resolveAndRun(Runnable(action))
}
