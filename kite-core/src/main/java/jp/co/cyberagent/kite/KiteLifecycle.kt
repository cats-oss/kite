package jp.co.cyberagent.kite

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

val KiteDslScope.currentState: Lifecycle.State
  get() = lifecycleOwner.lifecycle.currentState

inline fun KiteDslScope.onCreate(
  crossinline onCreate: () -> Unit
) = onLifecycleEvent(onCreate = onCreate)

inline fun KiteDslScope.onStart(
  crossinline onStart: () -> Unit
) = onLifecycleEvent(onStart = onStart)

inline fun KiteDslScope.onResume(
  crossinline onResume: () -> Unit
) = onLifecycleEvent(onResume = onResume)

inline fun KiteDslScope.onPause(
  crossinline onPause: () -> Unit
) = onLifecycleEvent(onPause = onPause)

inline fun KiteDslScope.onStop(
  crossinline onStop: () -> Unit
) = onLifecycleEvent(onStop = onStop)

inline fun KiteDslScope.onDestroy(
  crossinline onDestroy: () -> Unit
) = onLifecycleEvent(onDestroy = onDestroy)

@Suppress("LongParameterList")
inline fun KiteDslScope.onLifecycleEvent(
  crossinline onCreate: () -> Unit = {},
  crossinline onStart: () -> Unit = {},
  crossinline onResume: () -> Unit = {},
  crossinline onPause: () -> Unit = {},
  crossinline onStop: () -> Unit = {},
  crossinline onDestroy: () -> Unit = {}
) {
  lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
    override fun onCreate(owner: LifecycleOwner) {
      onCreate.invoke()
    }

    override fun onStart(owner: LifecycleOwner) {
      onStart.invoke()
    }

    override fun onResume(owner: LifecycleOwner) {
      onResume.invoke()
    }

    override fun onPause(owner: LifecycleOwner) {
      onPause.invoke()
    }

    override fun onStop(owner: LifecycleOwner) {
      onStop.invoke()
    }

    override fun onDestroy(owner: LifecycleOwner) {
      onDestroy.invoke()
    }
  })
}
