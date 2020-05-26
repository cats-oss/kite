package jp.co.cyberagent.kite.epoxy.paging

import com.airbnb.epoxy.EpoxyModel

typealias PagedListItemTransformer<T> = (Int, T?) -> EpoxyModel<*>
