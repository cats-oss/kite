package jp.co.cyberagent.kite.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class KiteCoroutineDispatchers(
  val default: CoroutineDispatcher = Dispatchers.Default,
  val main: CoroutineDispatcher = Dispatchers.Main,
  val io: CoroutineDispatcher = Dispatchers.IO
)

val KiteDslScope.defaultDispatcher: CoroutineDispatcher
  get() = requireByType<KiteCoroutineDispatchers>().default

val KiteDslScope.mainDispatcher: CoroutineDispatcher
  get() = requireByType<KiteCoroutineDispatchers>().main

val KiteDslScope.ioDispatcher: CoroutineDispatcher
  get() = requireByType<KiteCoroutineDispatchers>().io
