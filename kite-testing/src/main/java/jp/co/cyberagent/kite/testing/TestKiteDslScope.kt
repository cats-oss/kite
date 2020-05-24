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

/**
 * A scope for testing kite DSL.
 *
 * The scope provides some default contextual value for running test synchronously.
 *
 * - All dispatchers in [KiteCoroutineDispatchers] become the [TestCoroutineDispatcher] of the [testCoroutineScope].
 * - [KiteStateCreator] creates thread unsafe state and the value of state will update immediately.
 * - [MainThreadChecker] always assert current thread is the main thread.
 *
 * You can override above default contextual value via [kiteContext].
 *
 * @param kiteContext additional context.
 * @param testCoroutineScope the coroutine scope of the [KiteDslScope].
 */
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

/**
 * Runs kite DSL test inside a [TestKiteDslScope].
 *
 * @param kiteContext initiate the [TestKiteDslScope] with this additional context.
 * @param testCoroutineScope initiate the [TestCoroutineScope] with this [TestCoroutineDispatcher].
 * @param block invoke this test block inside the [TestKiteDslScope].
 */
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
