package jp.co.cyberagent.kite.sample.timeline.ui

import android.widget.Toast
import androidx.fragment.app.Fragment
import jp.co.cyberagent.kite.KiteDslScope
import jp.co.cyberagent.kite.getContextualValue
import jp.co.cyberagent.kite.onStart
import jp.co.cyberagent.kite.sample.databinding.FragmentTimelineExampleBinding
import jp.co.cyberagent.kite.sample.timeline.state.TimelineUseCase
import jp.co.cyberagent.kite.subscribe

fun KiteDslScope.bindTimelineExampleFragmentUi(
  timelineUseCase: TimelineUseCase
) {
  val binding = FragmentTimelineExampleBinding.bind(
    getContextualValue<Fragment>().requireView()
  )

  binding.swipeRefreshLayout.setOnRefreshListener {
    timelineUseCase.fetchTimeline.invoke()
  }

  onStart {
    timelineUseCase.fetchTimeline.invoke()
  }

  subscribe {
    timelineUseCase.timelineState.value.error?.let {
      Toast.makeText(getContextualValue(), it.message, Toast.LENGTH_SHORT).show()
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
