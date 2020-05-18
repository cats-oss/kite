package jp.co.cyberagent.kite.core

import kotlin.reflect.KProperty

interface KiteState

interface KiteGetter<out T> : KiteState {
  val value: T
}

interface KiteProperty<T> : KiteGetter<T> {
  override var value: T
}

fun <T> KiteProperty<T>.update(f: (prev: T) -> T) {
  value = f(value)
}

operator fun <T> KiteProperty<T>.setValue(thisRef: Nothing?, prop: KProperty<*>, value: T) {
  this.value = value
}

operator fun <T> KiteGetter<T>.getValue(thisRef: Nothing?, prop: KProperty<*>): T {
  return value
}
