package jp.co.cyberagent.kite

fun <T> KiteDslScope.testState(initialValue: () -> T): KiteProperty<T> {
  return object : AbstractKiteProperty<T>(ctx) {
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
