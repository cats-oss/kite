package jp.co.cyberagent.kite.sample.timeline.ui

import androidx.recyclerview.widget.RecyclerView
import jp.co.cyberagent.kite.core.KiteDslScope
import jp.co.cyberagent.kite.core.KiteGetter
import jp.co.cyberagent.kite.epoxy.epoxyDsl
import jp.co.cyberagent.kite.runtime.onStart
import jp.co.cyberagent.kite.runtime.onStop
import jp.co.cyberagent.kite.sample.timeline.epoxymodel.ContentModel
import jp.co.cyberagent.kite.sample.timeline.state.TimelineState
import jp.co.cyberagent.kite.sample.timeline.state.UpdateIsFavorite

fun KiteDslScope.bindTimeline(
  recyclerView: RecyclerView,
  timelineState: KiteGetter<TimelineState>,
  updateIsFavorite: UpdateIsFavorite
) {
  val observer = object : RecyclerView.AdapterDataObserver() {
    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
      recyclerView.smoothScrollToPosition(0)
    }
  }

  onStart {
    recyclerView.adapter?.registerAdapterDataObserver(observer)
  }

  onStop {
    recyclerView.adapter?.unregisterAdapterDataObserver(observer)
  }

  epoxyDsl(recyclerView) {
    config {
      isDebugLoggingEnabled = true
    }

    isReady {
      !timelineState.value.isLoading
    }

    buildModels {
      val timeline = timelineState.value.timeline
      timeline.contents.map {
        ContentModel(
          id = it.id,
          content = it.text,
          isFavorite = timeline.isFavorite.getOrElse(it.id) { false },
          onClick = updateIsFavorite
        ).id(it.id).addTo(this)
      }
    }
  }
}
