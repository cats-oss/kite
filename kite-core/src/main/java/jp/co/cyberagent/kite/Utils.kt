package jp.co.cyberagent.kite

import android.os.Looper

internal val isMainThread: Boolean
  get() = Looper.getMainLooper() === Looper.myLooper()

internal fun checkIsMainThread(name: String) {
  check(isMainThread) { "Cannot invoke $name in background thread." }
}
