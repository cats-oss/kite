package jp.co.cyberagent.kite.testing

import jp.co.cyberagent.kite.core.MainThreadChecker

class AlwaysMainThreadChecker : MainThreadChecker {
  override val isMainThread: Boolean = true
}
