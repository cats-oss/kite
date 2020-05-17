package jp.co.cyberagent.kite

import java.util.concurrent.ConcurrentHashMap

interface KiteContext {

  val keys: List<Any>

  operator fun <T : Any> set(key: Any, value: T)

  operator fun <T : Any> get(key: Any): T?

  operator fun plus(ctx: KiteContext): KiteContext
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

  override fun plus(ctx: KiteContext): KiteContext {
    if (this == ctx) return this
    val ctxKeys = ctx.keys
    val combinedCtx = KiteContext()
    ctxKeys.forEach { k ->
      combinedCtx[k] = ctx.require<Any>(k)
    }
    map.filterKeys { it !in ctxKeys }.forEach { (k, v) ->
      combinedCtx[k] = v
    }
    return combinedCtx
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
  val ctx = KiteContext()
  ctx += value
  return this + ctx
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
  val ctx = KiteContext()
  pair.forEach { (k, v) -> ctx[k] = v }
  return ctx
}

fun KiteDslScope.withKiteContext(
  extraCtx: KiteContext,
  body: KiteDslScope.() -> Unit
) {
  KiteDslScope(this, ctx + extraCtx).apply(body)
}
