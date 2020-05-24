package jp.co.cyberagent.kite.runtime

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import jp.co.cyberagent.kite.core.KiteDslScope
import jp.co.cyberagent.kite.core.requireByType

/**
 * Returns the current [Lifecycle.State].
 */
val KiteDslScope.currentState: Lifecycle.State
  get() = kiteContext.requireByType<LifecycleOwner>().lifecycle.currentState

/**
 * Executes the action [onCreate] when receives the lifecycle event [Lifecycle.Event.ON_CREATE].
 */
inline fun KiteDslScope.onCreate(
  crossinline onCreate: () -> Unit
) = onLifecycleEvent(onCreate = onCreate)

/**
 * Executes the action [onStart] when receives the lifecycle event [Lifecycle.Event.ON_START].
 */
inline fun KiteDslScope.onStart(
  crossinline onStart: () -> Unit
) = onLifecycleEvent(onStart = onStart)

/**
 * Executes the action [onResume] when receives the lifecycle event [Lifecycle.Event.ON_RESUME].
 */
inline fun KiteDslScope.onResume(
  crossinline onResume: () -> Unit
) = onLifecycleEvent(onResume = onResume)

/**
 * Executes the action [onPause] when receives the lifecycle event [Lifecycle.Event.ON_PAUSE].
 */
inline fun KiteDslScope.onPause(
  crossinline onPause: () -> Unit
) = onLifecycleEvent(onPause = onPause)

/**
 * Executes the action [onStop] when receives the lifecycle event [Lifecycle.Event.ON_STOP].
 */
inline fun KiteDslScope.onStop(
  crossinline onStop: () -> Unit
) = onLifecycleEvent(onStop = onStop)

/**
 * Executes the action [onDestroy] when receives the lifecycle event [Lifecycle.Event.ON_DESTROY].
 */
inline fun KiteDslScope.onDestroy(
  crossinline onDestroy: () -> Unit
) = onLifecycleEvent(onDestroy = onDestroy)

/**
 * Executes the specific action when receives the corresponded lifecycle event.
 */
@Suppress("LongParameterList")
inline fun KiteDslScope.onLifecycleEvent(
  crossinline onCreate: () -> Unit = {},
  crossinline onStart: () -> Unit = {},
  crossinline onResume: () -> Unit = {},
  crossinline onPause: () -> Unit = {},
  crossinline onStop: () -> Unit = {},
  crossinline onDestroy: () -> Unit = {}
) {
  val lifecycleOwner = kiteContext.requireByType<LifecycleOwner>()
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
