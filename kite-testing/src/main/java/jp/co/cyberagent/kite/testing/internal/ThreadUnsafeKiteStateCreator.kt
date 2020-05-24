package jp.co.cyberagent.kite.testing.internal

import jp.co.cyberagent.kite.core.AbstractKiteMutableState
import jp.co.cyberagent.kite.core.KiteMutableState
import jp.co.cyberagent.kite.core.KiteStateCreator

private class ThreadUnsafeKiteState<T>(
  initialValue: () -> T
) : AbstractKiteMutableState<T>() {

  override var value: T = initialValue.invoke()
    get() {
      subscribe()
      return field
    }
    set(value) {
      field = value
      notifyChanged()
    }
}

internal class ThreadUnsafeKiteStateCreator : KiteStateCreator {

  override fun <T> create(initialValue: () -> T): KiteMutableState<T> {
    return ThreadUnsafeKiteState(initialValue)
  }
}
