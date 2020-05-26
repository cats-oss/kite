package jp.co.cyberagent.kite.epoxy.paging.internal

import android.os.Handler
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.paging.PagedListEpoxyController
import jp.co.cyberagent.kite.epoxy.paging.PagedListEpoxyModelListBuilder
import jp.co.cyberagent.kite.epoxy.paging.PagedListItemTransformer

internal class KitePagedListEpoxyController<T>(
  configureBlock: (KitePagedListEpoxyController<T>.() -> Unit)?,
  private val buildModelsBlock: PagedListEpoxyModelListBuilder.() -> Unit,
  private val transformer: PagedListItemTransformer<T>,
  modelBuildingHandler: Handler = defaultModelBuildingHandler,
  diffingHandler: Handler = defaultDiffingHandler
) : PagedListEpoxyController<T>(modelBuildingHandler, diffingHandler) {

  init {
    configureBlock?.invoke(this)
  }

  var force: Boolean = false

  override fun buildItemModel(currentPosition: Int, item: T?): EpoxyModel<*> {
    return transformer.invoke(currentPosition, item)
  }

  override fun addModels(models: List<EpoxyModel<*>>) {
    val mergedModels = PagedListEpoxyModelListBuilder(models).apply(buildModelsBlock).build()
    add(mergedModels)
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
