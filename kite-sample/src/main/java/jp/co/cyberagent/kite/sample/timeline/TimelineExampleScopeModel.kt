package jp.co.cyberagent.kite.sample.timeline

import jp.co.cyberagent.kite.KiteScopeModel

class TimelineExampleScopeModel(
  repository: TimelineRepository
) : KiteScopeModel() {

  init {
    addService(repository, TimelineRepository::class)
  }
}
