package jp.co.cyberagent.kite.testing

import jp.co.cyberagent.kite.core.AbstractKiteProperty
import jp.co.cyberagent.kite.core.KiteDslScope
import jp.co.cyberagent.kite.core.KiteProperty

fun <T> KiteDslScope.testState(initialValue: T): KiteProperty<T> {
  return object : AbstractKiteProperty<T>(ctx) {
    override var value: T = initialValue
      get() {
        subscribe()
        return field
      }
      set(value) {
        if (field != value) {
          field = value
          notifyChanged()
        }
      }
  }
}
