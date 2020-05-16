package jp.co.cyberagent.kite

import android.os.Handler
import com.airbnb.epoxy.EpoxyController

@KiteDslMaker
class KiteEpoxyController internal constructor(
  private val builderList: List<KiteEpoxyController.() -> Unit>,
  private val isReady: () -> Boolean,
  modelBuildingHandler: Handler = defaultModelBuildingHandler,
  diffingHandler: Handler = defaultDiffingHandler
) : EpoxyController(modelBuildingHandler, diffingHandler) {

  override fun buildModels() {
    builderList.forEach { it.invoke(this) }
  }

  internal fun requestModelBuildIfReady() {
    if (isReady.invoke()) {
      requestModelBuild()
    }
  }
}
