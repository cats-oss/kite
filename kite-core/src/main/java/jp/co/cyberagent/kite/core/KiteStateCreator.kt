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
  val state = kiteContext.requireByType<KiteStateCreator>().create(initialValue)
  if (state is AbstractKiteMutableState) {
    state.subscriberManager = kiteContext.requireByType()
  }
  return state
}
