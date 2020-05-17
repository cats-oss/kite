package jp.co.cyberagent.kite.core

import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CoroutineScope

@Suppress("TestFunctionName")
fun TestKiteDslScope() = KiteDslScope(CoroutineScope(EmptyCoroutineContext), KiteContext())
