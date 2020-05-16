package jp.co.cyberagent.kite

import android.os.Handler
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyController

@KiteDslMaker
class KiteEpoxyDslScope {

  private val isReadyList = mutableListOf<KiteEpoxyIsReadyScope.() -> Boolean>()

  private val builderList = mutableListOf<KiteEpoxyController.() -> Unit>()

  var modelBuildingHandler: Handler = EpoxyController.defaultModelBuildingHandler

  var diffingHandler: Handler = EpoxyController.defaultDiffingHandler

  fun isReady(f: KiteEpoxyIsReadyScope.() -> Boolean) {
    isReadyList += f
  }

  fun buildModels(builder: KiteEpoxyController.() -> Unit) {
    builderList += builder
  }

  internal fun create(): KiteEpoxyController {
    val isReady = { isReadyList.all { KiteEpoxyIsReadyScope.run(it) } }
    return KiteEpoxyController(
      builderList,
      isReady,
      modelBuildingHandler,
      diffingHandler
    )
  }
}

fun KiteDslScope.epoxyDsl(
  recyclerView: RecyclerView,
  body: KiteEpoxyDslScope.() -> Unit
) {
  val scope = KiteEpoxyDslScope().apply(body)
  val controller = scope.create()
  recyclerView.adapter = controller.adapter
  subscribe {
    controller.requestModelBuildIfReady()
  }
}
