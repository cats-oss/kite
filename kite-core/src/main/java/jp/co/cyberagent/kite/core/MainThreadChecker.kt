package jp.co.cyberagent.kite.core

/**
 * A helper interface to determine current thread is main thread or not.
 */
interface MainThreadChecker {
  val isMainThread: Boolean
}

/**
 * Asserts current thread is main thread. If it is not, then throws [IllegalStateException].
 */
fun MainThreadChecker.checkIsMainThread(name: String) {
  check(isMainThread) { "Cannot invoke $name in background thread." }
}
