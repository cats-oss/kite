package jp.co.cyberagent.kite.core

import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * Persistent context for the [KiteDslScope]. It behaves like a map.
 */
interface KiteContext {

  /**
   * Returns all keys in this context.
   */
  val keys: List<Any>

  /**
   * Associates the [value] with the given [key]. If the key already existed then
   * throws [IllegalStateException].
   */
  operator fun <T : Any> set(key: Any, value: T)

  /**
   * Returns the value with the given [key]. If the key is not found then returns null.
   */
  operator fun <T : Any> get(key: Any): T?

  /**
   * Returns a context containing value from this context and values from other [kiteContext].
   * The elements from this context with the same key as in the other one are dropped.
   */
  operator fun plus(kiteContext: KiteContext): KiteContext
}

/**
 * Creates an empty [KiteContext].
 */
@Suppress("FunctionName")
fun KiteContext(): KiteContext = KiteContextImpl()

/**
 * Associates the key of [element] with its value. If the [key] already existed then
 * throws [IllegalStateException]
 */
operator fun KiteContext.plusAssign(element: KiteContextElement) {
  set(element.key, element.value)
}

/**
 * Returns a context containing key-value from this context and the [element].
 */
operator fun KiteContext.plus(element: KiteContextElement): KiteContext {
  val kiteContext = KiteContext()
  kiteContext += element
  return this + kiteContext
}

/**
 * Returns the value for the given key. If the key is not found, calls the [creator] function,
 * associates its result with the given key and returns it.
 */
fun <T : Any> KiteContext.setIfAbsent(key: Any, creator: () -> T): T {
  var value = get<T>(key)
  if (value == null) {
    value = creator.invoke()
    set(key, value)
  }
  return value
}

/**
 * Returns the value with the given [key]. If the key is not found then
 * throws [IllegalArgumentException].
 */
fun <T : Any> KiteContext.require(key: Any): T {
  return requireNotNull(get(key)) {
    "Context value $key not found."
  }
}

/**
 * Associates the [value] with its [KClass] as the key. If the key already existed then
 * throws [IllegalStateException].
 */
inline fun <reified T : Any> KiteContext.setByType(value: T) {
  set(T::class, value)
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
 * Returns a new [KiteContext] with the specified contents, given as a list of pairs
 * where the first value is the key and the second is the value.
 */
fun kiteContextOf(vararg element: KiteContextElement): KiteContext {
  val kiteContext = KiteContext()
  element.forEach { (k, v) -> kiteContext[k] = v }
  return kiteContext
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

private class KiteContextImpl : KiteContext {

  companion object {
    private const val KOTLIN_REFLECTION_WARNING = "(Kotlin reflection is not available)"
  }

  private val map = ConcurrentHashMap<Any, Any>()

  override val keys: List<Any>
    get() = map.keys().toList()

  override fun <T : Any> set(key: Any, value: T) {
    check(!map.containsKey(key)) {
      "Context value $key already added."
    }
    map[key] = value
  }

  override fun <T : Any> get(key: Any): T? {
    @Suppress("UNCHECKED_CAST")
    return map[key] as T?
  }

  override fun plus(kiteContext: KiteContext): KiteContext {
    if (this == kiteContext) return this
    val kiteContextKeys = kiteContext.keys
    val combinedKiteContext = KiteContext()
    kiteContextKeys.forEach { k ->
      combinedKiteContext[k] = kiteContext.require<Any>(k)
    }
    map.filterKeys { it !in kiteContextKeys }.forEach { (k, v) ->
      combinedKiteContext[k] = v
    }
    return combinedKiteContext
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
