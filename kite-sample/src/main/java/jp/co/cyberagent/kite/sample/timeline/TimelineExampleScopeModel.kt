package jp.co.cyberagent.kite.sample.timeline

import jp.co.cyberagent.kite.KiteComponentScopeModel

class TimelineExampleScopeModel(
  private val repository: TimelineRepository = TimelineRepository()
) : KiteComponentScopeModel() {

  init {
    addService(repository, TimelineRepository::class)
  }
}
