package jp.co.cyberagent.kite.core

interface KiteStateCreator {
  fun <T> create(initialValue: () -> T): KiteProperty<T>
}

fun <T> KiteDslScope.state(initialValue: () -> T): KiteProperty<T> {
  return requireByType<KiteStateCreator>().create(initialValue)
}
