package jp.co.cyberagent.kite.internal

import java.util.ArrayDeque
import jp.co.cyberagent.kite.KiteContext
import jp.co.cyberagent.kite.KiteState
import jp.co.cyberagent.kite.checkIsMainThread
import jp.co.cyberagent.kite.isMainThread
import jp.co.cyberagent.kite.setIfAbsent

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

internal val KiteContext.subscriberManager: KiteStateSubscriberManager
  get() = setIfAbsent(KiteStateSubscriberManager::class) { KiteStateSubscriberManager() }
