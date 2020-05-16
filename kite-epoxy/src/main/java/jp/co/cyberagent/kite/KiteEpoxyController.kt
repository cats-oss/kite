package jp.co.cyberagent.kite

import android.os.Handler
import com.airbnb.epoxy.EpoxyController

@KiteDslMaker
class KiteEpoxyController internal constructor(
  private val isReady: () -> Boolean,
  config: KiteEpoxyController.() -> Unit,
  private val builder: KiteEpoxyController.() -> Unit,
  modelBuildingHandler: Handler = defaultModelBuildingHandler,
  diffingHandler: Handler = defaultDiffingHandler
) : EpoxyController(modelBuildingHandler, diffingHandler) {

  init {
    config.invoke(this)
  }

  override fun buildModels() {
    builder.invoke(this)
  }

  internal fun requestModelBuildIfReady() {
    if (isReady.invoke()) {
      requestModelBuild()
    }
  }
}
