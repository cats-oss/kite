package jp.co.cyberagent.kite.epoxy.paging

import com.airbnb.epoxy.EpoxyModel
import jp.co.cyberagent.kite.epoxy.EpoxyModelListBuilder

class PagedListEpoxyModelListBuilder(
  val pagedListModels: List<EpoxyModel<*>>
) : EpoxyModelListBuilder() {

  companion object {
    val DEFAULT_BLOCK: PagedListEpoxyModelListBuilder.() -> Unit = {
      +pagedListModels
    }
  }
}
