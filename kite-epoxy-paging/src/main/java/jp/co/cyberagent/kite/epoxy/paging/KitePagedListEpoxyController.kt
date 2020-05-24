package jp.co.cyberagent.kite.epoxy.paging

import android.os.Handler
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.paging.PagedListEpoxyController
import jp.co.cyberagent.kite.core.KiteDslMaker

@KiteDslMaker
internal class KitePagedListEpoxyController<T>(
  config: EpoxyController.() -> Unit,
  private val transformer: (Int, T?) -> EpoxyModel<*>,
  private val modelBuilder: EpoxyController.(List<EpoxyModel<*>>) -> Unit,
  modelBuildingHandler: Handler = defaultModelBuildingHandler,
  diffingHandler: Handler = defaultDiffingHandler
) : PagedListEpoxyController<T>(modelBuildingHandler, diffingHandler) {

  init {
    config.invoke(this)
  }

  var force: Boolean = false

  override fun buildItemModel(currentPosition: Int, item: T?): EpoxyModel<*> {
    return transformer.invoke(currentPosition, item)
  }

  override fun addModels(models: List<EpoxyModel<*>>) {
    modelBuilder.invoke(this, models)
  }

  override fun requestModelBuild() {
    if (force) {
      requestForcedModelBuild()
      force = false
    } else {
      super.requestModelBuild()
    }
  }
}
