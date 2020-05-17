package jp.co.cyberagent.kite.core.internal

import java.util.ArrayDeque
import jp.co.cyberagent.kite.common.checkIsMainThread
import jp.co.cyberagent.kite.common.isMainThread
import jp.co.cyberagent.kite.core.KiteContext
import jp.co.cyberagent.kite.core.KiteState
import jp.co.cyberagent.kite.core.setIfAbsent

internal class KiteStateSubscriberManager {

  private val stateSubscriberMap: MutableMap<KiteState, LinkedHashSet<Runnable>> =
    mutableMapOf()

  private val runningSubscriberQueue: ArrayDeque<Runnable> = ArrayDeque()

  fun runAndResolveDependentState(runnable: Runnable) {
    checkIsMainThread("runAndResolveDependentState")
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
