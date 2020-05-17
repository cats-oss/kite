package jp.co.cyberagent.kite.testing

import androidx.lifecycle.LifecycleOwner
import jp.co.cyberagent.kite.KiteContext
import jp.co.cyberagent.kite.KiteDslScope
import jp.co.cyberagent.kite.KiteScopeModel
import jp.co.cyberagent.kite.setIfAbsent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScope

@ExperimentalCoroutinesApi
class TestKiteDslScope(
  val testCoroutineScope: TestCoroutineScope = TestCoroutineScope(),
  override val ctx: KiteContext = KiteContext()
) : KiteDslScope,
  KiteContext by ctx,
  TestCoroutineScope by testCoroutineScope {

  init {
    setIfAbsent(LifecycleOwner::class) { TestLifecycleOwner() }
    setIfAbsent(KiteScopeModel::class) { KiteScopeModel() }
  }
}

@ExperimentalCoroutinesApi
fun runTestKiteDsl(
  body: TestKiteDslScope.() -> Unit
) {
  TestKiteDslScope().run(body)
}
