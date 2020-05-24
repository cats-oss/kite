package jp.co.cyberagent.kite.epoxy

import android.os.Handler
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyController
import jp.co.cyberagent.kite.core.KiteDslMaker
import jp.co.cyberagent.kite.core.KiteDslScope
import jp.co.cyberagent.kite.core.MainThreadChecker
import jp.co.cyberagent.kite.core.checkIsMainThread
import jp.co.cyberagent.kite.core.requireByType
import jp.co.cyberagent.kite.core.subscribe

@KiteDslMaker
interface KiteEpoxyDslScope {

  fun config(block: EpoxyConfig)

  fun buildModels(modelBuilder: EpoxyModelBuilder)
}

private class KiteEpoxyDslScopeImpl : KiteEpoxyDslScope {

  private val configList = mutableListOf<EpoxyConfig>()

  private val modelBuilderList = mutableListOf<EpoxyModelBuilder>()

  var modelBuildingHandler: Handler = EpoxyController.defaultModelBuildingHandler

  var diffingHandler: Handler = EpoxyController.defaultDiffingHandler

  override fun config(block: EpoxyConfig) {
    configList += block
  }

  override fun buildModels(modelBuilder: EpoxyModelBuilder) {
    modelBuilderList += modelBuilder
  }

  fun create(): KiteEpoxyController {
    val configList = configList.toList()
    val config: EpoxyController.() -> Unit = {
      configList.forEach {
        it.invoke(KiteEpoxyConfigureControllerScope(), this)
      }
    }
    val modelBuilderList = modelBuilderList.toList()
    val builder: EpoxyController.() -> Unit = {
      modelBuilderList.forEach {
        it.invoke(KiteEpoxyBuildModelScope(), this)
      }
    }
    return KiteEpoxyController(
      config = config,
      modelBuilder = builder,
      modelBuildingHandler = modelBuildingHandler,
      diffingHandler = diffingHandler
    )
  }
}

fun KiteDslScope.epoxyDsl(
  recyclerView: RecyclerView,
  block: KiteEpoxyDslScope.() -> Unit
) {
  kiteContext.requireByType<MainThreadChecker>().checkIsMainThread("epoxyDsl")
  val scope = KiteEpoxyDslScopeImpl().apply(block)
  val controller = scope.create()
  recyclerView.adapter = controller.adapter
  subscribe {
    controller.requestModelBuild()
  }
}
