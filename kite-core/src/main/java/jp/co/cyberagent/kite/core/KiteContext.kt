package jp.co.cyberagent.kite.core

import java.util.concurrent.ConcurrentHashMap

interface KiteContext {

  val keys: List<Any>

  operator fun <T : Any> set(key: Any, value: T)

  operator fun <T : Any> get(key: Any): T?

  operator fun plus(kiteContext: KiteContext): KiteContext
}

private class KiteContextImpl : KiteContext {

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
}

@Suppress("FunctionName")
fun KiteContext(): KiteContext = KiteContextImpl()

fun <T : Any> KiteContext.setIfAbsent(key: Any, creator: () -> T): T {
  var value = get<T>(key)
  if (value == null) {
    value = creator.invoke()
    set(key, value)
  }
  return value
}

fun <T : Any> KiteContext.require(key: Any): T {
  return requireNotNull(get(key)) {
    "Context value $key not found, please ensure added it."
  }
}

inline operator fun <reified T : Any> KiteContext.plusAssign(value: T) {
  setByType(value)
}

inline operator fun <reified T : Any> KiteContext.plus(value: T): KiteContext {
  val kiteContext = KiteContext()
  kiteContext += value
  return this + kiteContext
}

inline fun <reified T : Any> KiteContext.setByType(value: T) {
  set(T::class, value)
}

inline fun <reified T : Any> KiteContext.getByType(): T? {
  return get<T>(T::class)
}

inline fun <reified T : Any> KiteContext.requireByType(): T {
  return require(T::class)
}

fun kiteContextOf(vararg pair: Pair<Any, Any>): KiteContext {
  val kiteContext = KiteContext()
  pair.forEach { (k, v) -> kiteContext[k] = v }
  return kiteContext
}

fun KiteDslScope.withKiteContext(
  extraKiteContext: KiteContext,
  body: KiteDslScope.() -> Unit
) {
  KiteDslScope(this, kiteContext + extraKiteContext).apply(body)
}
