package jp.co.cyberagent.kite.testing.internal

import jp.co.cyberagent.kite.core.MainThreadChecker

internal class AlwaysTrueMainThreadChecker : MainThreadChecker {
  override val isMainThread: Boolean = true
}
