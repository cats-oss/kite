package jp.co.cyberagent.kite.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * A set of [CoroutineDispatcher] and will be set into the [KiteContext].
 * This allows the coroutine to become testable with providing testable [CoroutineDispatcher].
 */
class KiteCoroutineDispatchers(
  val default: CoroutineDispatcher = Dispatchers.Default,
  val main: CoroutineDispatcher = Dispatchers.Main,
  val io: CoroutineDispatcher = Dispatchers.IO
)

/**
 * Returns the [KiteCoroutineDispatchers.default] in current context.
 */
val KiteDslScope.defaultDispatcher: CoroutineDispatcher
  get() = kiteContext.requireByType<KiteCoroutineDispatchers>().default

/**
 * Returns the [KiteCoroutineDispatchers.main] in current context.
 */
val KiteDslScope.mainDispatcher: CoroutineDispatcher
  get() = kiteContext.requireByType<KiteCoroutineDispatchers>().main

/**
 * Returns the [KiteCoroutineDispatchers.io] in this current context.
 */
val KiteDslScope.ioDispatcher: CoroutineDispatcher
  get() = kiteContext.requireByType<KiteCoroutineDispatchers>().io
