package jp.co.cyberagent.kite.core

import jp.co.cyberagent.kite.common.MainThreadChecker

class TestMainThreadChecker : MainThreadChecker {
  override val isMainThread: Boolean
    get() = Thread.currentThread().name.contains("kotest-engine")
}
