package jp.co.cyberagent.kite.runtime

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.observe
import jp.co.cyberagent.kite.common.checkIsMainThread
import jp.co.cyberagent.kite.common.isMainThread
import jp.co.cyberagent.kite.core.AbstractKiteProperty
import jp.co.cyberagent.kite.core.KiteContext
import jp.co.cyberagent.kite.core.KiteDslScope
import jp.co.cyberagent.kite.core.KiteProperty
import jp.co.cyberagent.kite.core.requireByType
import jp.co.cyberagent.kite.runtime.internal.createStateKey

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
  val liveData = requireByType<KiteScopeModel>().createTagIfAbsent(createStateKey()) {
    MutableLiveData(initialValue.invoke())
  }
  return KiteLiveDataBackedProperty(requireByType(), liveData, ctx)
}
