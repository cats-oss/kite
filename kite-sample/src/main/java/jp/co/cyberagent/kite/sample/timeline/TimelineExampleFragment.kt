package jp.co.cyberagent.kite.sample.timeline

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import jp.co.cyberagent.kite.KiteDslScope
import jp.co.cyberagent.kite.KiteGetter
import jp.co.cyberagent.kite.epoxyDsl
import jp.co.cyberagent.kite.kiteDsl
import jp.co.cyberagent.kite.onStart
import jp.co.cyberagent.kite.onStop
import jp.co.cyberagent.kite.sample.R
import jp.co.cyberagent.kite.sample.databinding.FragmentTimelineExampleBinding
import jp.co.cyberagent.kite.subscribe

class TimelineExampleFragment : Fragment(R.layout.fragment_timeline_example) {

  private lateinit var scopeModelFactory: ViewModelProvider.Factory

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    scopeModelFactory = object : ViewModelProvider.Factory {
      override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return TimelineExampleScopeModel(TimelineRepository()) as T
      }
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) = kiteDsl(
    scopeModelFactory = scopeModelFactory
  ) {
    val binding = FragmentTimelineExampleBinding.bind(requireView())

    val (timelineState, fetchTimeline, updateIsFavorite) = useTimeline()

    binding.swipeRefreshLayout.setOnRefreshListener {
      fetchTimeline.invoke()
    }

    onStart {
      fetchTimeline.invoke()
    }

    subscribe {
      timelineState.value.error?.let {
        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
      }
    }

    subscribe {
      binding.swipeRefreshLayout.isRefreshing = timelineState.value.isLoading
    }

    bindTimeline(binding.recyclerView, timelineState, updateIsFavorite)
  }
}

private fun KiteDslScope.bindTimeline(
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
