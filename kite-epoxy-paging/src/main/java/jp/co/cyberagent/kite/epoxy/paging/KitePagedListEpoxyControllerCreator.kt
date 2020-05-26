package jp.co.cyberagent.kite.epoxy.paging

import androidx.paging.PagedList
import com.airbnb.epoxy.paging.PagedListEpoxyController
import jp.co.cyberagent.kite.core.KiteDslScope
import jp.co.cyberagent.kite.core.KiteState
import jp.co.cyberagent.kite.core.subscribe
import jp.co.cyberagent.kite.epoxy.EpoxyControllerCreator
import jp.co.cyberagent.kite.epoxy.paging.internal.KitePagedListEpoxyController

class KitePagedListEpoxyControllerCreator<T> :
  EpoxyControllerCreator<PagedListEpoxyController<T>>() {

  private var _buildModelsBlock: (PagedListEpoxyModelListBuilder.() -> Unit)? = null
  private val buildModelsBlock: PagedListEpoxyModelListBuilder.() -> Unit
    get() = _buildModelsBlock ?: PagedListEpoxyModelListBuilder.DEFAULT_BLOCK

  private var _transformer: (PagedListItemTransformer<T>)? = null
  private val transformer: PagedListItemTransformer<T>
    get() = checkNotNull(_transformer) {
      "PagedListItemTransformer has not been set."
    }

  var needForceRebuildStates: List<KiteState<*>> = emptyList()

  fun buildModels(block: PagedListEpoxyModelListBuilder.() -> Unit) {
    _buildModelsBlock = block
  }

  fun transformer(transformer: PagedListItemTransformer<T>) {
    _transformer = transformer
  }

  fun forceRebuildFor(vararg state: KiteState<*>) {
    needForceRebuildStates = state.toList()
  }

  override fun create(): PagedListEpoxyController<T> {
    return KitePagedListEpoxyController(
      configureBlock = configureBlock,
      buildModelsBlock = buildModelsBlock,
      transformer = transformer,
      modelBuildingHandler = modelBuildingHandler,
      diffingHandler = diffingHandler
    )
  }
}

fun <T> KiteDslScope.createPagedListController(
  subscribe: Boolean = true,
  pagedListState: KiteState<PagedList<T>>,
  block: KitePagedListEpoxyControllerCreator<T>.() -> Unit
): PagedListEpoxyController<T> {
  val creator = KitePagedListEpoxyControllerCreator<T>().apply(block)
  val controller = creator.create()
  subscribe {
    creator.needForceRebuildStates.forEach {
      it.value
    }
    (controller as? KitePagedListEpoxyController)?.force = true
  }
  if (subscribe) {
    subscribe {
      controller.submitList(pagedListState.value)
    }
    subscribe {
      controller.requestForcedModelBuild()
    }
  }
  return controller
}
