package jp.co.cyberagent.kite.core.internal

import java.util.ArrayDeque
import jp.co.cyberagent.kite.core.KiteState
import jp.co.cyberagent.kite.core.MainThreadChecker
import jp.co.cyberagent.kite.core.checkIsMainThread

/**
 * A helper class to auto detect the dependent [KiteState] of a [Subscriber].
 */
internal class KiteStateSubscriberManager(
  private val mainThreadChecker: MainThreadChecker
) {

  private val stateSubscriberMap: MutableMap<KiteState<*>, LinkedHashSet<Subscriber<*>>> =
    mutableMapOf()

  private val runningSubscriberQueue: ArrayDeque<Subscriber<*>> = ArrayDeque()

  fun <T> runAndSubscribe(subscriber: Subscriber<T>): T {
    mainThreadChecker.checkIsMainThread("runAndResolveDependentState")
    runningSubscriberQueue.push(subscriber)
    val result = subscriber()
    runningSubscriberQueue.pop()
    return result
  }

  fun subscribeTo(state: KiteState<*>) {
    if (mainThreadChecker.isMainThread) {
      val runnable = runningSubscriberQueue.peek() ?: return
      if (runnable is RefOnlySubscriber) return
      val subscribers = stateSubscriberMap.getOrPut(state) { linkedSetOf() }
      subscribers += runnable
    }
  }

  fun notifyStateChanged(state: KiteState<*>) {
    mainThreadChecker.checkIsMainThread("notifyStateChanged")
    stateSubscriberMap[state]?.toList()?.forEach { runAndSubscribe(it) }
  }
}
