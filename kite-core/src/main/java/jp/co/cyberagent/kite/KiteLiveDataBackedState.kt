package jp.co.cyberagent.kite

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import jp.co.cyberagent.kite.internal.KiteStateSubscriberManager
import jp.co.cyberagent.kite.internal.createStateKey
import jp.co.cyberagent.kite.internal.subscriberManager

private class KiteLiveDataBackedStateObserver<T>(
  private val state: KiteState,
  private val subscriberManager: KiteStateSubscriberManager
) : Observer<T> {

  private var preValue: Any? = Unset

  override fun onChanged(t: T) {
    if (preValue != t) {
      preValue = t
      subscriberManager.notifyStateChanged(state)
    }
  }
}

private class KiteLiveDataBackedProperty<T>(
  private val liveData: MutableLiveData<T>,
  private val subscriberManager: KiteStateSubscriberManager
) : KiteProperty<T> {

  override var value: T
    get() {
      subscriberManager.subscribeTo(this)
      @Suppress("UNCHECKED_CAST")
      return liveData.value as T
    }
    set(value) {
      if (isMainThread) {
        liveData.value = value
      } else {
        liveData.postValue(value)
      }
    }
}

fun <T> KiteDslScope.state(initialValue: () -> T): KiteProperty<T> {
  val liveData = scopeModel.createTagIfAbsent(createStateKey()) {
    MutableLiveData(initialValue.invoke())
  }
  val state = KiteLiveDataBackedProperty(liveData, subscriberManager)
  val observer = KiteLiveDataBackedStateObserver<T>(state, subscriberManager)
  liveData.observe(lifecycleOwner, observer)
  return state
}
