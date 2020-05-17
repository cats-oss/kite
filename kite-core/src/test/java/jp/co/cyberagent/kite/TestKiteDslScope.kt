package jp.co.cyberagent.kite

import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CoroutineScope

@Suppress("TestFunctionName")
fun TestKiteDslScope() = KiteDslScope(CoroutineScope(EmptyCoroutineContext), KiteContext())
