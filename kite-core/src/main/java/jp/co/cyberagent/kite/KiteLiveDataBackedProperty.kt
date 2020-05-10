package jp.co.cyberagent.kite

import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

private class KiteLiveDataBackedProperty<T>(
  initialValue: T,
  private val liveData: MutableLiveData<T>,
  private val dependencyManager: KiteStateDependencyManager
) : KiteProperty<T>, Observer<T> {

  private var _value: T = initialValue

  override var value: T
    get() {
      dependencyManager.registerDependency(this)
      return _value
    }
    set(value) {
      if (Looper.getMainLooper() === Looper.myLooper()) {
        liveData.value = value
      } else {
        liveData.postValue(value)
      }
    }

  override fun onChanged(t: T) {
    if (_value != t) {
      _value = t
      dependencyManager.notifyDependencyChanged(this)
    }
  }
}

fun <T> KiteDslScope.state(initialValue: T): KiteProperty<T> {
  val liveData = componentScopeModel.keepDuringLifecycle { MutableLiveData(initialValue) }
  @Suppress("UNCHECKED_CAST") val currentValue = liveData.value as T
  val observerState = KiteLiveDataBackedProperty(currentValue, liveData, stateDependencyManager)
  liveData.observe(lifecycleOwner, observerState)
  return observerState
}
