package jp.co.cyberagent.kite.sample.timeline.ui

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import jp.co.cyberagent.kite.core.KiteDslScope
import jp.co.cyberagent.kite.core.requireByType
import jp.co.cyberagent.kite.core.subscribe
import jp.co.cyberagent.kite.runtime.onStart
import jp.co.cyberagent.kite.sample.databinding.FragmentTimelineBinding
import jp.co.cyberagent.kite.sample.timeline.state.TimelineUseCase

fun KiteDslScope.bindTimelineExampleFragmentUi(
  timelineUseCase: TimelineUseCase
) {
  val binding = FragmentTimelineBinding.bind(
    kiteContext.requireByType<Fragment>().requireView()
  )
  val context = kiteContext.requireByType<Context>()

  binding.swipeRefreshLayout.setOnRefreshListener {
    timelineUseCase.fetchTimeline.invoke()
  }

  onStart {
    timelineUseCase.fetchTimeline.invoke()
  }

  subscribe {
    timelineUseCase.timelineState.value.error?.let {
      Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
    }
  }

  subscribe {
    binding.swipeRefreshLayout.isRefreshing = timelineUseCase.timelineState.value.isLoading
  }

  bindTimeline(
    binding.recyclerView,
    timelineUseCase.timelineState,
    timelineUseCase.updateIsFavorite
  )
}
