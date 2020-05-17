package jp.co.cyberagent.kite.testing

import jp.co.cyberagent.kite.AbstractKiteProperty
import jp.co.cyberagent.kite.KiteDslScope
import jp.co.cyberagent.kite.KiteProperty

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
