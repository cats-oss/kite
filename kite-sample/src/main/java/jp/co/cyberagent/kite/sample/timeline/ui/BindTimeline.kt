package jp.co.cyberagent.kite.sample.timeline.ui

import androidx.recyclerview.widget.RecyclerView
import jp.co.cyberagent.kite.core.KiteDslScope
import jp.co.cyberagent.kite.core.KiteState
import jp.co.cyberagent.kite.core.memo
import jp.co.cyberagent.kite.epoxy.createEpoxyController
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

  val contentModels = memo {
    val contents = timelineState.value.timeline.contents
    val isFavorite = timelineState.value.timeline.isFavorite
    contents.map {
      ContentModel(
        id = it.id,
        content = it.text,
        isFavorite = isFavorite.getOrElse(it.id) { false },
        onClick = updateIsFavorite
      )
    }
  }

  recyclerView.adapter = createEpoxyController {
    configure {
      isDebugLoggingEnabled = true
    }

    buildModels {
      +contentModels.value
    }
  }.adapter
}
