package jp.co.cyberagent.kite.sample.timeline

import android.widget.Toast
import androidx.fragment.app.Fragment
import jp.co.cyberagent.kite.epoxyDsl
import jp.co.cyberagent.kite.exceptionOrNull
import jp.co.cyberagent.kite.fragmentUi
import jp.co.cyberagent.kite.onStart
import jp.co.cyberagent.kite.sample.R
import jp.co.cyberagent.kite.sample.databinding.FragmentTimelineExampleBinding
import jp.co.cyberagent.kite.subscribe
import jp.co.cyberagent.kite.valueOrNull

class TimelineExampleFragment : Fragment(R.layout.fragment_timeline_example) {

  val ui = fragmentUi<TimelineExampleScopeModel> {
    val binding = FragmentTimelineExampleBinding.bind(requireView())

    val (timelineState, isFavoriteState, refreshTimeline, setFavorite) = useTimeline()

    binding.swipeRefreshLayout.setOnRefreshListener {
      refreshTimeline.invoke()
    }

    onStart {
      refreshTimeline.invoke()
    }

    subscribe {
      isFavoriteState.exceptionOrNull()?.let {
        val message = "Set Favorite Fail!"
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
      }
    }

    subscribe {
      binding.swipeRefreshLayout.isRefreshing = timelineState.value.isLoading()
    }

    epoxyDsl(binding.recyclerView) {

      isReady {
        !timelineState.value.isLoading()
      }

      buildModels {
        val timeline = timelineState.valueOrNull()!!
        timeline.contents.map {
          ContentModel(
            id = it.id,
            content = it.text,
            isFavorite = timeline.isFavorite.getOrElse(it.id) { false },
            onClick = setFavorite
          ).id(it.id).addTo(this)
        }
      }
    }
  }
}
