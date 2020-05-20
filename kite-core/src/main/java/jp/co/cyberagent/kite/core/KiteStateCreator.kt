package jp.co.cyberagent.kite.core

interface KiteStateCreator {
  fun <T> create(initialValue: () -> T): KiteMutableState<T>
}

fun <T> KiteDslScope.state(initialValue: () -> T): KiteMutableState<T> {
  return requireByType<KiteStateCreator>().create(initialValue)
}
