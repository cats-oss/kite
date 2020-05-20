package jp.co.cyberagent.kite.core

import kotlin.reflect.KProperty

interface KiteState<out T> {
  val value: T
}

interface KiteMutableState<T> : KiteState<T> {
  override var value: T
}

fun <T> KiteMutableState<T>.update(f: (prev: T) -> T) {
  value = f(value)
}

operator fun <T> KiteMutableState<T>.setValue(thisRef: Nothing?, prop: KProperty<*>, value: T) {
  this.value = value
}

operator fun <T> KiteState<T>.getValue(thisRef: Nothing?, prop: KProperty<*>): T {
  return value
}
