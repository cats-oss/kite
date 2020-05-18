package jp.co.cyberagent.kite.core

class TestMainThreadChecker : MainThreadChecker {
  override val isMainThread: Boolean
    get() = Thread.currentThread().name.contains("kotest-engine")
}
