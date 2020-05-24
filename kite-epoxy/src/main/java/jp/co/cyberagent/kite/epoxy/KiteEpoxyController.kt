package jp.co.cyberagent.kite.epoxy

import android.os.Handler
import com.airbnb.epoxy.EpoxyController

internal class KiteEpoxyController(
  config: EpoxyController.() -> Unit,
  private val modelBuilder: EpoxyController.() -> Unit,
  modelBuildingHandler: Handler = defaultModelBuildingHandler,
  diffingHandler: Handler = defaultDiffingHandler
) : EpoxyController(modelBuildingHandler, diffingHandler) {

  init {
    config.invoke(this)
  }

  override fun buildModels() {
    modelBuilder.invoke(this)
  }
}
