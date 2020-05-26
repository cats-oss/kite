package jp.co.cyberagent.kite.epoxy.internal

import android.os.Handler
import com.airbnb.epoxy.EpoxyController
import jp.co.cyberagent.kite.epoxy.EpoxyModelListBuilder

internal class KiteEpoxyController(
  configureBlock: (KiteEpoxyController.() -> Unit)?,
  private val buildModelsBlock: EpoxyModelListBuilder.() -> Unit,
  modelBuildingHandler: Handler = defaultModelBuildingHandler,
  diffingHandler: Handler = defaultDiffingHandler
) : EpoxyController(modelBuildingHandler, diffingHandler) {

  init {
    configureBlock?.invoke(this)
  }

  override fun buildModels() {
    val models = EpoxyModelListBuilder().apply(buildModelsBlock).build()
    add(models)
  }
}
