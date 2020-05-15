package jp.co.cyberagent.kite.sample.timeline

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import jp.co.cyberagent.kite.kiteDsl
import jp.co.cyberagent.kite.sample.R

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
    val timelineUseCase = useTimeline()
    bindTimelineExampleFragmentUi(timelineUseCase)
  }
}
