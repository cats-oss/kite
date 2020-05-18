package jp.co.cyberagent.kite.testing

import jp.co.cyberagent.kite.core.KiteContext
import jp.co.cyberagent.kite.core.KiteDslScope
import jp.co.cyberagent.kite.core.KiteStateCreator
import jp.co.cyberagent.kite.core.setIfAbsent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScope

@ExperimentalCoroutinesApi
class TestKiteDslScope(
  override val kiteContext: KiteContext = KiteContext(),
  private val testCoroutineScope: TestCoroutineScope = TestCoroutineScope()
) : KiteDslScope,
  KiteContext by kiteContext,
  TestCoroutineScope by testCoroutineScope {

  init {
    setIfAbsent(KiteStateCreator::class) { ThreadUnsafeKiteStateCreator(kiteContext) }
  }
}

@ExperimentalCoroutinesApi
fun runTestKiteDsl(
  body: TestKiteDslScope.() -> Unit
) {
  TestKiteDslScope().run(body)
}
