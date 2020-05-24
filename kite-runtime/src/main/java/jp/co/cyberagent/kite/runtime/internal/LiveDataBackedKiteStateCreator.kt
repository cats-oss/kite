package jp.co.cyberagent.kite.runtime.internal

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.observe
import jp.co.cyberagent.kite.core.AbstractKiteMutableState
import jp.co.cyberagent.kite.core.KiteMutableState
import jp.co.cyberagent.kite.core.KiteStateCreator
import jp.co.cyberagent.kite.core.MainThreadChecker
import jp.co.cyberagent.kite.core.checkIsMainThread

private class LiveDataBackedKiteMutableState<T>(
  lifecycleOwner: LifecycleOwner,
  private val liveData: MutableLiveData<T>,
  private val mainThreadChecker: MainThreadChecker
) : AbstractKiteMutableState<T>() {

  override var value: T
    get() {
      subscribe()
      @Suppress("UNCHECKED_CAST")
      return liveData.value as T
    }
    set(value) {
      if (mainThreadChecker.isMainThread) {
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
  private val lifecycleOwner: LifecycleOwner,
  private val scopeModel: KiteScopeModel,
  private val mainThreadChecker: MainThreadChecker
) : KiteStateCreator {

  private val keyGenerator: KiteStateKeyGenerator = KiteStateKeyGenerator()

  override fun <T> create(initialValue: () -> T): KiteMutableState<T> {
    mainThreadChecker.checkIsMainThread("state")
    val key = keyGenerator.createStateKey()
    val liveData = scopeModel.createTagIfAbsent(key) {
      MutableLiveData(initialValue.invoke())
    }
    return LiveDataBackedKiteMutableState(
      lifecycleOwner = lifecycleOwner,
      liveData = liveData,
      mainThreadChecker = mainThreadChecker
    )
  }
}
