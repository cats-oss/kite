package jp.co.cyberagent.kite

import android.os.Handler
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyController

@KiteDslMaker
class KiteEpoxyDslScope {

  private val isReadyList = mutableListOf<IsReady>()

  private val builderList = mutableListOf<EpoxyControllerBuildModelScope>()

  var modelBuildingHandler: Handler = EpoxyController.defaultModelBuildingHandler

  var diffingHandler: Handler = EpoxyController.defaultDiffingHandler

  fun isReady(f: () -> Boolean) {
    isReadyList += f
  }

  fun buildModels(builder: EpoxyController.() -> Unit) {
    builderList += builder
  }

  fun create(): KiteEpoxyController {
    val isReady = { isReadyList.all { it.invoke() } }
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
