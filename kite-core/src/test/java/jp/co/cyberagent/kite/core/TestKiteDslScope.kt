package jp.co.cyberagent.kite.core

import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.EmptyCoroutineContext

private class TestKiteStateCreator : KiteStateCreator {
  override fun <T> create(initialValue: () -> T): KiteMutableState<T> {
    return object : AbstractKiteMutableState<T>() {
      override var value: T = initialValue()
        get() {
          subscribe()
          return field
        }
        set(value) {
          field = value
          notifyChanged()
        }
    }
  }
}

@Suppress("TestFunctionName")
fun TestKiteDslScope(
  kiteContext: KiteContext = KiteContext()
) = KiteDslScope(
  CoroutineScope(EmptyCoroutineContext),
  buildKiteContext {
    setByType<KiteStateCreator>(TestKiteStateCreator())
    setByType<MainThreadChecker>(TestMainThreadChecker())
  } + kiteContext
)
