package jp.co.cyberagent.kite.testing

import jp.co.cyberagent.kite.core.KiteContext
import jp.co.cyberagent.kite.core.KiteCoroutineDispatchers
import jp.co.cyberagent.kite.core.KiteDslScope
import jp.co.cyberagent.kite.core.KiteStateCreator
import jp.co.cyberagent.kite.core.MainThreadChecker
import jp.co.cyberagent.kite.core.buildKiteContext
import jp.co.cyberagent.kite.core.setByType
import jp.co.cyberagent.kite.testing.internal.AlwaysTrueMainThreadChecker
import jp.co.cyberagent.kite.testing.internal.ThreadUnsafeKiteStateCreator
import kotlin.coroutines.ContinuationInterceptor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope

@ExperimentalCoroutinesApi
class TestKiteDslScope(
  kiteContext: KiteContext = KiteContext(),
  private val testCoroutineScope: TestCoroutineScope = TestCoroutineScope()
) : KiteDslScope by KiteDslScope(
    coroutineScope = testCoroutineScope,
    kiteContext = buildKiteContext {
      val dispatcher =
        testCoroutineScope.coroutineContext[ContinuationInterceptor] as TestCoroutineDispatcher
      setByType(KiteCoroutineDispatchers(dispatcher, dispatcher, dispatcher))
      setByType<KiteStateCreator>(ThreadUnsafeKiteStateCreator())
      setByType<MainThreadChecker>(AlwaysTrueMainThreadChecker())
    } + kiteContext
  )

@ExperimentalCoroutinesApi
fun runTestKiteDsl(
  kiteContext: KiteContext = KiteContext(),
  testCoroutineScope: TestCoroutineScope = TestCoroutineScope(),
  block: TestKiteDslScope.() -> Unit
) {
  TestKiteDslScope(
    kiteContext = kiteContext,
    testCoroutineScope = testCoroutineScope
  ).run(block)
}
