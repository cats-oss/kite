package jp.co.cyberagent.kite.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.createGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.fragment
import jp.co.cyberagent.kite.sample.counter.CounterFragment
import jp.co.cyberagent.kite.sample.timeline.TimelineFragment

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment

    navHostFragment.navController.apply {
      graph = createGraph(MainGraph.id, MainGraph.Dest.home) {
        fragment<HomeFragment>(MainGraph.Dest.home) {
          action(MainGraph.Action.toCounterExample) {
            destinationId = MainGraph.Dest.counterExample
          }
          action(MainGraph.Action.toTimelineExample) {
            destinationId = MainGraph.Dest.timelineExample
          }
        }

        fragment<CounterFragment>(MainGraph.Dest.counterExample)

        fragment<TimelineFragment>(MainGraph.Dest.timelineExample)
      }
    }
  }
}
