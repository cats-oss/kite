package jp.co.cyberagent.kite

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

private class KiteLiveDataBackedGetter<T>(
  initialValue: T,
  private val dependencyManager: KiteStateDependencyManager
) : KiteGetter<T>, Observer<T> {

  private var _value: T = initialValue

  override val value: T
    get() {
      dependencyManager.registerDependency(this)
      return _value
    }

  override fun onChanged(t: T) {
    if (_value != t) {
      _value = t
      dependencyManager.notifyDependencyChanged(this)
    }
  }
}

fun <T> KiteDslScope.state(liveData: LiveData<T>): KiteGetter<T> {
  @Suppress("UNCHECKED_CAST") val currentValue = liveData.value as T
  val observerState = KiteLiveDataBackedGetter(currentValue, stateDependencyManager)
  liveData.observe(lifecycleOwner, observerState)
  return observerState
}
