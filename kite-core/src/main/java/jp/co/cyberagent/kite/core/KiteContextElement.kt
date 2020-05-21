package jp.co.cyberagent.kite.core

import kotlin.reflect.KClass

typealias KiteContextElement = Pair<Any, Any>

internal val KiteContextElement.key: Any
  get() = first

internal val KiteContextElement.value: Any
  get() = second

/**
 * Creates a [KiteContextElement] with the [KClass] [T]
 */
inline fun <reified T : Any> T.asKiteContextElement(): Pair<KClass<*>, Any> {
  return T::class to this
}
