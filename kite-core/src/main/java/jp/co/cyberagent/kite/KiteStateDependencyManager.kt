package jp.co.cyberagent.kite

class KiteStateDependencyManager {

  private val dependencyMap: MutableMap<KiteState, LinkedHashSet<Runnable>> =
    mutableMapOf()

  private var currentRunnable: Runnable? = null

  fun resolveAndRun(runnable: Runnable) {
    val prev = currentRunnable
    currentRunnable = runnable
    runnable.run()
    currentRunnable = prev
  }

  fun registerDependency(state: KiteState) {
    val runnable = currentRunnable ?: return
    val depSet = dependencyMap.getOrPut(state) { linkedSetOf() }
    depSet += runnable
  }

  fun notifyDependencyChanged(state: KiteState) {
    dependencyMap[state]?.forEach { resolveAndRun(it) }
  }
}
