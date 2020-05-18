package jp.co.cyberagent.kite.core

fun <T> KiteDslScope.testState(initialValue: () -> T): KiteProperty<T> {
  return object : AbstractKiteProperty<T>(kiteContext) {
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
}
