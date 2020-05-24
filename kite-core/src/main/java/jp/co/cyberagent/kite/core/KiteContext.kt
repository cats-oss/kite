package jp.co.cyberagent.kite.core

import kotlin.reflect.KClass

/**
 * Persistent context for the [KiteDslScope]. It behaves like a map.
 */
interface KiteContext {

  /**
   * Returns all keys in this context.
   */
  val keys: Set<Any>

  /**
   * Returns the value with the given [key]. If the key is not found then returns null.
   */
  operator fun <T : Any> get(key: Any): T?

  /**
   * Returns a context containing value from this context and values from other [kiteContext].
   * The elements from this context with the same key as in the other one are dropped.
   */
  operator fun plus(kiteContext: KiteContext): KiteContext {
    if (this == kiteContext) return this
    val base = this
    return buildKiteContext {
      val mergedKeys = base.keys + kiteContext.keys
      mergedKeys.forEach { key ->
        val value: Any = kiteContext[key] ?: base.require(key)
        set(key, value)
      }
    }
  }
}

/**
 * A modifiable [KiteContext].
 */
interface MutableKiteContext : KiteContext {
  /**
   * Associates the [value] with the given [key]. If the key already existed then
   * throws [IllegalStateException].
   */
  operator fun <T : Any> set(key: Any, value: T)
}

/**
 * Returns the value with the given [key]. If the key is not found then
 * throws [IllegalArgumentException].
 */
fun <T : Any> KiteContext.require(key: Any): T {
  return requireNotNull(get(key)) {
    "Context value with key $key not found."
  }
}

/**
 * Returns the value with its [KClass] as the key. If the key is not found then returns null.
 */
inline fun <reified T : Any> KiteContext.getByType(): T? {
  return get<T>(T::class)
}

/**
 * Returns the value with its [KClass] as the key. If the key is not found then
 * throws [IllegalArgumentException].
 */
inline fun <reified T : Any> KiteContext.requireByType(): T {
  return require(T::class)
}

/**
 * Associates the [value] with its [KClass] as the key. If the key already existed then
 * throws [IllegalStateException].
 */
inline fun <reified T : Any> MutableKiteContext.setByType(value: T) {
  set(T::class, value)
}

/**
 * Associates the key of [element] with its value. If the [key] already existed then
 * throws [IllegalStateException]
 */
operator fun MutableKiteContext.plusAssign(element: KiteContextElement) {
  set(element.key, element.value)
}

/**
 * Creates an empty [KiteContext].
 */
@Suppress("FunctionName")
fun KiteContext(): KiteContext = EmptyKiteContext

/**
 * Builds a new [KiteContext] by populating a [MutableKiteContext] using the given [builderAction].
 */
fun buildKiteContext(
  builderAction: MutableKiteContext.() -> Unit
): KiteContext {
  return KiteContextImpl().apply(builderAction)
}

/**
 * Returns a new [KiteContext] with the specified contents, given as a list of pairs
 * where the first value is the key and the second is the value.
 */
fun kiteContextOf(vararg element: KiteContextElement): KiteContext {
  return if (element.isEmpty()) {
    EmptyKiteContext
  } else {
    buildKiteContext {
      element.forEach { (k, v) -> set(k, v) }
    }
  }
}

/**
 * Calls the kite DSL with a given [KiteContext].
 *
 * The resulting context for the [block] is derived by merging the current [KiteContext] with the
 * specified [context] using the [KiteContext.plus] method.
 */
fun KiteDslScope.withKiteContext(
  context: KiteContext,
  block: KiteDslScope.() -> Unit
) {
  KiteDslScope(this, kiteContext + context).apply(block)
}

private object EmptyKiteContext : KiteContext {

  override val keys: Set<Any> get() = emptySet()

  override fun <T : Any> get(key: Any): T? = null
}

private class KiteContextImpl : MutableKiteContext {

  companion object {
    private const val KOTLIN_REFLECTION_WARNING = "(Kotlin reflection is not available)"
  }

  private val map = mutableMapOf<Any, Any>()

  override val keys: Set<Any>
    get() = map.keys

  override fun <T : Any> set(key: Any, value: T) {
    check(!map.containsKey(key)) {
      "Cannot set with existing key $key."
    }
    map[key] = value
  }

  override fun <T : Any> get(key: Any): T? {
    @Suppress("UNCHECKED_CAST")
    return map[key] as T?
  }

  override fun toString(): String {
    return buildString {
      appendln("KiteContext Element:")
      map
        .mapKeys { (key, _) ->
          key.toString().replace(KOTLIN_REFLECTION_WARNING, "").trim()
        }
        .toSortedMap()
        .forEach { (key, value) ->
          appendln("\t$key = $value")
        }
    }
  }
}
