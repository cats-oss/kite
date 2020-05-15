package jp.co.cyberagent.kite.sample.timeline

import jp.co.cyberagent.kite.KiteScopeModel
import jp.co.cyberagent.kite.sample.timeline.data.TimelineRepository

class TimelineExampleScopeModel(
  repository: TimelineRepository
) : KiteScopeModel() {

  init {
    addService(repository, TimelineRepository::class)
  }
}
