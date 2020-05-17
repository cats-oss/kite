package jp.co.cyberagent.kite.sample.timeline

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import jp.co.cyberagent.kite.runtime.KiteScopeModelFactory
import jp.co.cyberagent.kite.runtime.kiteDsl
import jp.co.cyberagent.kite.sample.R
import jp.co.cyberagent.kite.sample.timeline.data.TimelineRepository
import jp.co.cyberagent.kite.sample.timeline.state.useTimeline
import jp.co.cyberagent.kite.sample.timeline.ui.bindTimelineExampleFragmentUi

class TimelineFragment : Fragment(R.layout.fragment_timeline) {

  private lateinit var scopeModelFactory: KiteScopeModelFactory

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    scopeModelFactory = KiteScopeModelFactory().apply {
      addService(TimelineRepository())
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) = kiteDsl(
    scopeModelFactory = scopeModelFactory
  ) {
    val timelineUseCase = useTimeline()
    bindTimelineExampleFragmentUi(timelineUseCase)
  }
}
