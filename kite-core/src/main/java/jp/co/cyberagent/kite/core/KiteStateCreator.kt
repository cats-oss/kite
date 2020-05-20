package jp.co.cyberagent.kite.core

/**
 * Defines how to create [KiteMutableState].
 */
interface KiteStateCreator {
  fun <T> create(initialValue: () -> T): KiteMutableState<T>
}

/**
 * Find the [KiteStateCreator] in current context and use it to create
 * a [KiteState] with [initialValue].
 */
fun <T> KiteDslScope.state(initialValue: () -> T): KiteMutableState<T> {
  return requireByType<KiteStateCreator>().create(initialValue)
}
