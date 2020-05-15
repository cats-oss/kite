package jp.co.cyberagent.kite.sample.timeline

import androidx.recyclerview.widget.RecyclerView
import jp.co.cyberagent.kite.KiteDslScope
import jp.co.cyberagent.kite.KiteGetter
import jp.co.cyberagent.kite.epoxyDsl
import jp.co.cyberagent.kite.onStart
import jp.co.cyberagent.kite.onStop

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
