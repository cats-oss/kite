package jp.co.cyberagent.kite.core.internal

import java.util.ArrayDeque
import jp.co.cyberagent.kite.core.KiteState
import jp.co.cyberagent.kite.core.MainThreadChecker
import jp.co.cyberagent.kite.core.checkIsMainThread

/**
 * A helper class to auto detect the dependent [KiteState] of a [Runnable].
 */
internal class KiteStateSubscriberManager(
  private val mainThreadChecker: MainThreadChecker
) {

  private val stateSubscriberMap: MutableMap<KiteState<*>, LinkedHashSet<Runnable>> =
    mutableMapOf()

  private val runningSubscriberQueue: ArrayDeque<Runnable> = ArrayDeque()

  fun runAndResolveDependentState(runnable: Runnable) {
    mainThreadChecker.checkIsMainThread("runAndResolveDependentState")
    runningSubscriberQueue.push(runnable)
    runnable.run()
    runningSubscriberQueue.pop()
  }

  fun subscribeTo(state: KiteState<*>) {
    if (mainThreadChecker.isMainThread) {
      val runnable = runningSubscriberQueue.peek() ?: return
      val depSet = stateSubscriberMap.getOrPut(state) { linkedSetOf() }
      depSet += runnable
    }
  }

  fun notifyStateChanged(state: KiteState<*>) {
    mainThreadChecker.checkIsMainThread("notifyStateChanged")
    stateSubscriberMap[state]?.forEach { runAndResolveDependentState(it) }
  }
}
