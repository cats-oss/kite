package jp.co.cyberagent.kite.testing

import jp.co.cyberagent.kite.core.KiteContext
import jp.co.cyberagent.kite.core.KiteCoroutineDispatchers
import jp.co.cyberagent.kite.core.KiteDslScope
import jp.co.cyberagent.kite.core.KiteStateCreator
import jp.co.cyberagent.kite.core.MainThreadChecker
import jp.co.cyberagent.kite.core.setIfAbsent
import jp.co.cyberagent.kite.testing.internal.AlwaysTrueMainThreadChecker
import jp.co.cyberagent.kite.testing.internal.ThreadUnsafeKiteStateCreator
import kotlin.coroutines.ContinuationInterceptor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope

@ExperimentalCoroutinesApi
class TestKiteDslScope(
  override val kiteContext: KiteContext = KiteContext(),
  private val testCoroutineScope: TestCoroutineScope = TestCoroutineScope()
) : KiteDslScope,
  KiteContext by kiteContext,
  TestCoroutineScope by testCoroutineScope {

  init {
    setIfAbsent(KiteCoroutineDispatchers::class) {
      val dispatcher = coroutineContext[ContinuationInterceptor] as TestCoroutineDispatcher
      KiteCoroutineDispatchers(dispatcher, dispatcher, dispatcher)
    }
    setIfAbsent(KiteStateCreator::class) { ThreadUnsafeKiteStateCreator(kiteContext) }
    setIfAbsent(MainThreadChecker::class) { AlwaysTrueMainThreadChecker() }
  }
}

@ExperimentalCoroutinesApi
fun runTestKiteDsl(
  kiteContext: KiteContext = KiteContext(),
  testCoroutineScope: TestCoroutineScope = TestCoroutineScope(),
  body: TestKiteDslScope.() -> Unit
) {
  TestKiteDslScope(
    kiteContext = kiteContext,
    testCoroutineScope = testCoroutineScope
  ).run(body)
}
