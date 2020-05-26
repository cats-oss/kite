package jp.co.cyberagent.kite.epoxy

import com.airbnb.epoxy.EpoxyModel
import jp.co.cyberagent.kite.core.KiteDslMaker

@KiteDslMaker
open class EpoxyModelListBuilder {

  private val models: MutableList<EpoxyModel<*>> = mutableListOf()

  operator fun EpoxyModel<*>.unaryPlus() {
    models += this
  }

  operator fun Collection<EpoxyModel<*>>.unaryPlus() {
    models += this
  }

  fun build(): List<EpoxyModel<*>> {
    return models.toList()
  }
}
