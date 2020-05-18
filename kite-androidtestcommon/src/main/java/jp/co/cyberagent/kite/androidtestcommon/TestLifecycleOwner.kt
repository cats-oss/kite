package jp.co.cyberagent.kite.androidtestcommon

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

class TestLifecycleOwner : LifecycleOwner {

  val lifecycle = LifecycleRegistry(this)

  override fun getLifecycle(): Lifecycle = lifecycle
}
