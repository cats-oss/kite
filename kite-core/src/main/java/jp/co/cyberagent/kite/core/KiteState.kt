package jp.co.cyberagent.kite.core

import kotlin.reflect.KProperty

/**
 * Represents a read-only state with single updatable [value].
 */
interface KiteState<out T> {
  val value: T
}

/**
 * Represents a mutable [KiteState] that provided a setter for [value].
 * Do not implemented this interface directly, instead inherits the abstract
 * class [AbstractKiteMutableState].
 */
interface KiteMutableState<T> : KiteState<T> {
  override var value: T
}

/**
 * Updates the [value] with the result of function [f]. The function [f] has one parameter that
 * receives current [value].
 */
fun <T> KiteMutableState<T>.update(f: (curr: T) -> T) {
  value = f(value)
}

/**
 * Supports delegated property.
 */
operator fun <T> KiteMutableState<T>.setValue(thisRef: Nothing?, prop: KProperty<*>, value: T) {
  this.value = value
}

/**
 * Supports delegated property.
 */
operator fun <T> KiteState<T>.getValue(thisRef: Nothing?, prop: KProperty<*>): T {
  return value
}
