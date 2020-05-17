package jp.co.cyberagent.kite.common

import android.os.Looper

val isMainThread: Boolean
  get() = Looper.getMainLooper() === Looper.myLooper()

fun checkIsMainThread(name: String) {
  check(isMainThread) { "Cannot invoke $name in background thread." }
}
