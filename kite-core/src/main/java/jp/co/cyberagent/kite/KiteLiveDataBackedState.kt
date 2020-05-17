package jp.co.cyberagent.kite

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.observe
import jp.co.cyberagent.kite.internal.createStateKey

private class KiteLiveDataBackedProperty<T>(
  lifecycleOwner: LifecycleOwner,
  private val liveData: MutableLiveData<T>,
  ctx: KiteContext
) : AbstractKiteProperty<T>(ctx) {

  override var value: T
    get() {
      subscribe()
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

  init {
    liveData.distinctUntilChanged().observe(lifecycleOwner) {
      notifyChanged()
    }
  }
}

fun <T> KiteDslScope.state(initialValue: () -> T): KiteProperty<T> {
  checkIsMainThread("state")
  val liveData = scopeModel.createTagIfAbsent(createStateKey()) {
    MutableLiveData(initialValue.invoke())
  }
  return KiteLiveDataBackedProperty(lifecycleOwner, liveData, ctx)
}
