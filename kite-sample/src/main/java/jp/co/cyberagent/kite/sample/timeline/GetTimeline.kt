package jp.co.cyberagent.kite.sample.timeline

import jp.co.cyberagent.kite.Invoker0
import jp.co.cyberagent.kite.Invoker2
import jp.co.cyberagent.kite.KiteDslScope
import jp.co.cyberagent.kite.KiteGetter
import jp.co.cyberagent.kite.Tuple3
import jp.co.cyberagent.kite.getService
import jp.co.cyberagent.kite.state
import jp.co.cyberagent.kite.update
import kotlinx.coroutines.Dispatchers

typealias FetchTimeline = Invoker0

typealias UpdateIsFavorite = Invoker2<String, Boolean>

data class Timeline(
  val contents: List<Content> = emptyList(),
  val isFavorite: Map<String, Boolean> = emptyMap()
)

data class TimelineState(
  val timeline: Timeline = Timeline(),
  val isLoading: Boolean = false,
  val error: Throwable? = null
)

fun KiteDslScope.useTimeline(): Tuple3<KiteGetter<TimelineState>, FetchTimeline, UpdateIsFavorite> {
  val repository = getService<TimelineRepository>()

  val timelineState = state(TimelineState())

  val fetchTimeline: FetchTimeline = {
    launch(Dispatchers.IO) {
      runCatching {
        timelineState.update { it.copy(isLoading = true, error = null) }
        val contents = repository.getTimelineContent()
        val isFavorite = repository.checkFavorite(contents.map { it.id })
        timelineState.update {
          it.copy(timeline = Timeline(contents, isFavorite), isLoading = false)
        }
      }.onFailure { e ->
        timelineState.update { it.copy(isLoading = false, error = e) }
      }
    }
  }

  val updateFavorite: UpdateIsFavorite = { id, to ->
    launch(Dispatchers.IO) {
      val from = timelineState.value.timeline.isFavorite[id] ?: return@launch
      if (from == to) return@launch
      runCatching {
        timelineState.update { s ->
          with(s) {
            copy(
              timeline = with(timeline) {
                copy(isFavorite = isFavorite + (id to to))
              },
              error = null
            )
          }
        }
        if (to) repository.addFavorite(id)
        else repository.removeFavorite(id)
      }.onFailure { e ->
        timelineState.update { s ->
          with(s) {
            copy(
              timeline = with(timeline) {
                copy(
                  isFavorite = isFavorite + (id to from)
                )
              },
              error = e
            )
          }
        }
      }
    }
  }

  return Tuple3(timelineState, fetchTimeline, updateFavorite)
}