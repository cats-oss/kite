package jp.co.cyberagent.kite.internal

import java.util.ArrayDeque
import jp.co.cyberagent.kite.KiteDslScope
import jp.co.cyberagent.kite.KiteState
import jp.co.cyberagent.kite.checkIsMainThread
import jp.co.cyberagent.kite.isMainThread
import jp.co.cyberagent.kite.setContextualValueIfAbsent

internal class KiteStateSubscriberManager {

  private val stateSubscriberMap: MutableMap<KiteState, LinkedHashSet<Runnable>> =
    mutableMapOf()

  private val runningSubscriberQueue: ArrayDeque<Runnable> = ArrayDeque()

  fun runAndResolveDependentState(runnable: Runnable) {
    checkIsMainThread("resolveDependentStateAndRun")
    runningSubscriberQueue.push(runnable)
    runnable.run()
    runningSubscriberQueue.pop()
  }

  fun subscribeTo(state: KiteState) {
    if (isMainThread) {
      val runnable = runningSubscriberQueue.peek() ?: return
      val depSet = stateSubscriberMap.getOrPut(state) { linkedSetOf() }
      depSet += runnable
    }
  }

  fun notifyStateChanged(state: KiteState) {
    checkIsMainThread("notifyStateChanged")
    stateSubscriberMap[state]?.forEach { runAndResolveDependentState(it) }
  }
}

internal val KiteDslScope.subscriberManager: KiteStateSubscriberManager
  get() = setContextualValueIfAbsent { KiteStateSubscriberManager() }
