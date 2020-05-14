package jp.co.cyberagent.kite

private class KiteMemoState<T>(
  private val computation: () -> T,
  private val dependencyManager: KiteStateDependencyManager
) : KiteGetter<T> {

  private var _value: Any? = Unset

  override val value: T
    get() {
      if (_value == Unset) {
        dependencyManager.resolveAndRun(
          Runnable {
            _value = computation.invoke()
            dependencyManager.notifyDependencyChanged(this)
          }
        )
      }
      dependencyManager.registerDependency(this)
      @Suppress("UNCHECKED_CAST")
      return _value as T
    }
}

fun <T> KiteDslScope.memo(
  computation: () -> T
): KiteGetter<T> {
  this as KiteDslScopeImpl
  return KiteMemoState(computation, stateDependencyManager)
}
