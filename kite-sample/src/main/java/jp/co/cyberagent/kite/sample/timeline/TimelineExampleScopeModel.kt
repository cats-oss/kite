package jp.co.cyberagent.kite.sample.timeline

import jp.co.cyberagent.kite.KiteComponentScopeModel

class TimelineExampleScopeModel(
  repository: TimelineRepository
) : KiteComponentScopeModel() {

  init {
    addService(repository, TimelineRepository::class)
  }
}
