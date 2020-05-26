package jp.co.cyberagent.kite.epoxy

import android.os.Handler
import com.airbnb.epoxy.EpoxyController
import jp.co.cyberagent.kite.core.KiteDslMaker

@KiteDslMaker
abstract class EpoxyControllerCreator<T : EpoxyController> {

  var modelBuildingHandler: Handler = EpoxyController.defaultModelBuildingHandler

  var diffingHandler: Handler = EpoxyController.defaultDiffingHandler

  protected var configureBlock: (T.() -> Unit)? = null

  fun configure(block: T.() -> Unit) {
    configureBlock = block
  }

  abstract fun create(): T
}
