package jp.co.cyberagent.kite

fun KiteDslScope.subscribe(
  action: () -> Unit
) {
  stateDependencyManager.resolveAndRun(Runnable(action))
}
