package jp.co.cyberagent.kite.epoxy

import com.airbnb.epoxy.EpoxyController
import jp.co.cyberagent.kite.core.KiteDslMaker

@KiteDslMaker
interface KiteEpoxyConfigureScope

private object KiteEpoxyConfigureScopeImpl : KiteEpoxyConfigureScope

@Suppress("FunctionName")
fun KiteEpoxyConfigureControllerScope(): KiteEpoxyConfigureScope = KiteEpoxyConfigureScopeImpl

typealias EpoxyConfig = KiteEpoxyConfigureScope.(EpoxyController) -> Unit
