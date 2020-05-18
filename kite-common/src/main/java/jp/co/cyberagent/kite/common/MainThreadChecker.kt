package jp.co.cyberagent.kite.common

interface MainThreadChecker {
  val isMainThread: Boolean
}

fun MainThreadChecker.checkIsMainThread(name: String) {
  check(isMainThread) { "Cannot invoke $name in background thread." }
}
