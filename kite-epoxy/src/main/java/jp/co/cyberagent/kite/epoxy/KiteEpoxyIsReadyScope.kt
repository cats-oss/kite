package jp.co.cyberagent.kite.epoxy

import jp.co.cyberagent.kite.core.KiteDslMaker

@KiteDslMaker
interface KiteEpoxyIsReadyScope

private object KiteEpoxyIsReadyScopeImpl : KiteEpoxyIsReadyScope

@Suppress("FunctionName")
internal fun KiteEpoxyIsReadyScope(): KiteEpoxyIsReadyScope = KiteEpoxyIsReadyScopeImpl
