package jp.co.cyberagent.kite

import android.os.Looper
import androidx.lifecycle.LiveData
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
      if (Looper.getMainLooper() === Looper.myLooper()) {
        liveData.value = value
      } else {
        liveData.postValue(value)
      }
    }
}

private class KiteLiveDataBackedGetter<T>(
  private val liveData: LiveData<T>,
  private val subscriberManager: KiteStateSubscriberManager
) : KiteGetter<T> {

  override val value: T
    get() {
      subscriberManager.subscribeTo(this)
      @Suppress("UNCHECKED_CAST")
      return liveData.value as T
    }
}

fun <T> KiteDslScope.state(liveData: LiveData<T>): KiteGetter<T> {
  val state = KiteLiveDataBackedGetter(liveData, subscriberManager)
  val observer = KiteLiveDataBackedStateObserver<T>(state, subscriberManager)
  liveData.observe(lifecycleOwner, observer)
  return state
}

fun <T> KiteDslScope.state(liveData: MutableLiveData<T>): KiteProperty<T> {
  val state = KiteLiveDataBackedProperty(liveData, subscriberManager)
  val observer = KiteLiveDataBackedStateObserver<T>(state, subscriberManager)
  liveData.observe(lifecycleOwner, observer)
  return state
}

fun <T> KiteDslScope.state(initialValue: T): KiteProperty<T> {
  val liveData = scopeModel.createTagIfAbsent(createStateKey()) { MutableLiveData(initialValue) }
  return state(liveData)
}
