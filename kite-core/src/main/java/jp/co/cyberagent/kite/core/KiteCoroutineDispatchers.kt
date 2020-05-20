package jp.co.cyberagent.kite.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * A set of [CoroutineDispatcher] and should be set into the [KiteContext].
 * This allows the coroutine to become testable with providing testable [CoroutineDispatcher].
 */
class KiteCoroutineDispatchers(
  val default: CoroutineDispatcher = Dispatchers.Default,
  val main: CoroutineDispatcher = Dispatchers.Main,
  val io: CoroutineDispatcher = Dispatchers.IO
)

/**
 * Returns the [KiteCoroutineDispatchers.default] in this [KiteContext].
 */
val KiteContext.defaultDispatcher: CoroutineDispatcher
  get() = requireByType<KiteCoroutineDispatchers>().default

/**
 * Returns the [KiteCoroutineDispatchers.main] in this [KiteContext].
 */
val KiteContext.mainDispatcher: CoroutineDispatcher
  get() = requireByType<KiteCoroutineDispatchers>().main

/**
 * Returns the [KiteCoroutineDispatchers.io] in this [KiteContext].
 */
val KiteContext.ioDispatcher: CoroutineDispatcher
  get() = requireByType<KiteCoroutineDispatchers>().io
