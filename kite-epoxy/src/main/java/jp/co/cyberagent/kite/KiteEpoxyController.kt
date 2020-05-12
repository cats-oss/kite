package jp.co.cyberagent.kite

import android.os.Handler
import com.airbnb.epoxy.EpoxyController

class KiteEpoxyController(
    private val builderList: List<EpoxyControllerBuildModelScope>,
    private val isReady: IsReady,
    modelBuildingHandler: Handler = defaultModelBuildingHandler,
    diffingHandler: Handler = defaultDiffingHandler
) : EpoxyController(modelBuildingHandler, diffingHandler) {

  override fun buildModels() {
    builderList.forEach { it.invoke(this) }
  }

  fun requestModelBuildIfReady() {
    if (isReady.invoke()) {
      requestModelBuild()
    }
  }
}
