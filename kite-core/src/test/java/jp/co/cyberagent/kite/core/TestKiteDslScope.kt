package jp.co.cyberagent.kite.core

import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CoroutineScope

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
