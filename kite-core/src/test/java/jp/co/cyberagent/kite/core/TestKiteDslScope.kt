package jp.co.cyberagent.kite.core

import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CoroutineScope

private class TestKiteStateCreator(
  private val kiteContext: KiteContext
) : KiteStateCreator {
  override fun <T> create(initialValue: () -> T): KiteMutableState<T> {
    return object : AbstractKiteMutableState<T>(kiteContext) {
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
fun TestKiteDslScope() = KiteDslScope(
  CoroutineScope(EmptyCoroutineContext),
  KiteContext().apply {
    setByType<KiteStateCreator>(TestKiteStateCreator(this))
    setByType<MainThreadChecker>(TestMainThreadChecker())
  }
)
