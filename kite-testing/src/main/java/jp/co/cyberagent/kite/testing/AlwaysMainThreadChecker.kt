package jp.co.cyberagent.kite.testing

import jp.co.cyberagent.kite.common.MainThreadChecker

class AlwaysMainThreadChecker : MainThreadChecker {
  override val isMainThread: Boolean = true
}
