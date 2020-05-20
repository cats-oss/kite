package jp.co.cyberagent.kite.testing.internal

import jp.co.cyberagent.kite.core.AbstractKiteProperty
import jp.co.cyberagent.kite.core.KiteContext
import jp.co.cyberagent.kite.core.KiteProperty
import jp.co.cyberagent.kite.core.KiteStateCreator

private class ThreadUnsafeKiteState<T>(
  initialValue: () -> T,
  kiteContext: KiteContext
) : AbstractKiteProperty<T>(kiteContext) {

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

internal class ThreadUnsafeKiteStateCreator(
  private val kiteContext: KiteContext
) : KiteStateCreator {

  override fun <T> create(initialValue: () -> T): KiteProperty<T> {
    return ThreadUnsafeKiteState(initialValue, kiteContext)
  }
}
