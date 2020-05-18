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
import jp.co.cyberagent.kite.core.KiteStateCreator
import jp.co.cyberagent.kite.core.requireByType
import jp.co.cyberagent.kite.runtime.internal.createStateKey

private class LiveDataBackedKiteProperty<T>(
  lifecycleOwner: LifecycleOwner,
  private val liveData: MutableLiveData<T>,
  kiteContext: KiteContext
) : AbstractKiteProperty<T>(kiteContext) {

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

internal class LiveDataBackedKiteStateCreator(
  private val kiteContext: KiteContext
) : KiteStateCreator {

  override fun <T> create(initialValue: () -> T): KiteProperty<T> {
    checkIsMainThread("state")
    val key = kiteContext.createStateKey()
    val liveData = kiteContext.requireByType<KiteScopeModel>().createTagIfAbsent(key) {
      MutableLiveData(initialValue.invoke())
    }
    return LiveDataBackedKiteProperty(kiteContext.requireByType(), liveData, kiteContext)
  }
}
