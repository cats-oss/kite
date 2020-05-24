package jp.co.cyberagent.kite.sample.timeline.ui

import androidx.recyclerview.widget.RecyclerView
import jp.co.cyberagent.kite.core.KiteDslScope
import jp.co.cyberagent.kite.core.KiteState
import jp.co.cyberagent.kite.epoxy.epoxyDsl
import jp.co.cyberagent.kite.runtime.onStart
import jp.co.cyberagent.kite.runtime.onStop
import jp.co.cyberagent.kite.sample.timeline.epoxymodel.ContentModel
import jp.co.cyberagent.kite.sample.timeline.state.TimelineState
import jp.co.cyberagent.kite.sample.timeline.state.UpdateIsFavorite

fun KiteDslScope.bindTimeline(
  recyclerView: RecyclerView,
  timelineState: KiteState<TimelineState>,
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
      it.isDebugLoggingEnabled = true
    }

    buildModels { controller ->
      val timeline = timelineState.value.timeline
      timeline.contents.forEach {
        ContentModel(
          id = it.id,
          content = it.text,
          isFavorite = timeline.isFavorite.getOrElse(it.id) { false },
          onClick = updateIsFavorite
        ).id(it.id).addTo(controller)
      }
    }
  }
}
