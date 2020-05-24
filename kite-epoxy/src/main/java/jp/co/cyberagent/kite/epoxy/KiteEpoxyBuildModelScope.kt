package jp.co.cyberagent.kite.epoxy

import com.airbnb.epoxy.EpoxyController
import jp.co.cyberagent.kite.core.KiteDslMaker

@KiteDslMaker
interface KiteEpoxyBuildModelScope

private object KiteEpoxyBuildModelScopeImpl : KiteEpoxyBuildModelScope

@Suppress("FunctionName")
fun KiteEpoxyBuildModelScope(): KiteEpoxyBuildModelScope = KiteEpoxyBuildModelScopeImpl

typealias EpoxyModelBuilder = KiteEpoxyBuildModelScope.(EpoxyController) -> Unit
