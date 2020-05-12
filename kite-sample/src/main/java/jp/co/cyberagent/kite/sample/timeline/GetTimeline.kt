package jp.co.cyberagent.kite.sample.timeline

import jp.co.cyberagent.kite.Invoker0
import jp.co.cyberagent.kite.Invoker2
import jp.co.cyberagent.kite.KiteDslScope
import jp.co.cyberagent.kite.KiteGetter
import jp.co.cyberagent.kite.KiteProperty
import jp.co.cyberagent.kite.Tuple4
import jp.co.cyberagent.kite.UiState
import jp.co.cyberagent.kite.failure
import jp.co.cyberagent.kite.getService
import jp.co.cyberagent.kite.loading
import jp.co.cyberagent.kite.state
import jp.co.cyberagent.kite.success
import jp.co.cyberagent.kite.update
import jp.co.cyberagent.kite.valueOrNull
import kotlinx.coroutines.Dispatchers

typealias MutableMyTimelineState = KiteProperty<UiState<MyTimeline>>

typealias MyTimelineState = KiteGetter<UiState<MyTimeline>>

typealias RefreshTimeline = Invoker0

typealias FavoriteState = KiteProperty<UiState<Unit>>

typealias SetFavorite = Invoker2<String, Boolean>

data class MyTimeline(
  val contents: List<Content>,
  val isFavorite: Map<String, Boolean>
)

fun KiteDslScope.useTimeline(): Tuple4<MyTimelineState, FavoriteState, RefreshTimeline, SetFavorite> {

  val repository = getService<TimelineRepository>()

  val timelineState: MutableMyTimelineState = state(loading())

  val favoriteState: KiteProperty<UiState<Unit>> = state(Unit.success())

  val refreshTimeline: RefreshTimeline = {
    launch(Dispatchers.IO) {
      timelineState.update {
        val timeline = repository.getTimeline()
        val isFavorite = repository.checkFavorite(timeline.map { it.id })
        MyTimeline(timeline, isFavorite)
      }
    }
  }

  val setFavorite: SetFavorite = { id, to ->
    launch(Dispatchers.IO) {
      val timeline = timelineState.valueOrNull() ?: return@launch
      val isFavorite = timeline.isFavorite[id] ?: return@launch
      if (isFavorite == to) return@launch
      runCatching {
        timelineState.value =
          timeline.copy(isFavorite = timeline.isFavorite + (id to to)).success()
        if (to) repository.addFavorite(id)
        else repository.removeFavorite(id)
        favoriteState.value = Unit.success()
      }.onFailure {
        timelineState.value =
          timeline.copy(isFavorite = timeline.isFavorite + (id to isFavorite)).success()
        favoriteState.value = it.failure()
      }
    }
  }

  return Tuple4(timelineState, favoriteState, refreshTimeline, setFavorite)
}
