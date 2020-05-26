package jp.co.cyberagent.kite.epoxy

import com.airbnb.epoxy.EpoxyController
import jp.co.cyberagent.kite.core.KiteDslScope
import jp.co.cyberagent.kite.core.subscribe
import jp.co.cyberagent.kite.epoxy.internal.KiteEpoxyController

class KiteEpoxyControllerCreator : EpoxyControllerCreator<EpoxyController>() {

  private var _buildModelsBlock: (EpoxyModelListBuilder.() -> Unit)? = null
  private val buildModelsBlock: (EpoxyModelListBuilder.() -> Unit)
    get() = checkNotNull(_buildModelsBlock) { "KiteEpoxyModelListBuilder block has not been set." }

  fun buildModels(block: EpoxyModelListBuilder.() -> Unit) {
    _buildModelsBlock = block
  }

  override fun create(): EpoxyController {
    return KiteEpoxyController(
      configureBlock,
      buildModelsBlock,
      modelBuildingHandler,
      diffingHandler
    )
  }
}

fun KiteDslScope.createEpoxyController(
  subscribe: Boolean = true,
  block: KiteEpoxyControllerCreator.() -> Unit
): EpoxyController {
  val controller = KiteEpoxyControllerCreator().apply(block).create()
  if (subscribe) {
    subscribe { controller.requestModelBuild() }
  }
  return controller
}
