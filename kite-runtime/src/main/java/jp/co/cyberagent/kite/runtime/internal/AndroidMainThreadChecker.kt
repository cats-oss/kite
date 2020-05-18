package jp.co.cyberagent.kite.runtime.internal

import android.os.Looper
import jp.co.cyberagent.kite.common.MainThreadChecker

class AndroidMainThreadChecker : MainThreadChecker {
  override val isMainThread: Boolean
    get() = Looper.getMainLooper().thread === Thread.currentThread()
}
