package jp.co.cyberagent.kite.sample.timeline

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import jp.co.cyberagent.kite.core.KiteContext
import jp.co.cyberagent.kite.core.asKiteContextElement
import jp.co.cyberagent.kite.core.kiteContextOf
import jp.co.cyberagent.kite.runtime.kiteDsl
import jp.co.cyberagent.kite.sample.R
import jp.co.cyberagent.kite.sample.timeline.data.TimelineRepository
import jp.co.cyberagent.kite.sample.timeline.state.useTimeline
import jp.co.cyberagent.kite.sample.timeline.ui.bindTimelineExampleFragmentUi

class TimelineFragment : Fragment(R.layout.fragment_timeline) {

  private lateinit var timelineBaseKiteContext: KiteContext

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    timelineBaseKiteContext = kiteContextOf(
      TimelineRepository().asKiteContextElement()
    )
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) = kiteDsl(
    kiteContext = timelineBaseKiteContext
  ) {
    val timelineUseCase = useTimeline()
    bindTimelineExampleFragmentUi(timelineUseCase)
    Log.d("Timeline", kiteContext.toString())
  }
}
