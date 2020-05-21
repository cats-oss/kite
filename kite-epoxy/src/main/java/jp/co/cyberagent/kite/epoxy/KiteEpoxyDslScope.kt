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

  fun isReady(f: KiteEpoxyIsReadyScope.() -> Boolean)

  fun config(block: KiteEpoxyController.() -> Unit)

  fun buildModels(modelBuilder: KiteEpoxyController.() -> Unit)
}

private class KiteEpoxyDslScopeImpl : KiteEpoxyDslScope {

  private val isReadyList = mutableListOf<KiteEpoxyIsReadyScope.() -> Boolean>()

  private val configList = mutableListOf<KiteEpoxyController.() -> Unit>()

  private val modelBuilderList = mutableListOf<KiteEpoxyController.() -> Unit>()

  var modelBuildingHandler: Handler = EpoxyController.defaultModelBuildingHandler

  var diffingHandler: Handler = EpoxyController.defaultDiffingHandler

  override fun isReady(f: KiteEpoxyIsReadyScope.() -> Boolean) {
    isReadyList += f
  }

  override fun config(block: KiteEpoxyController.() -> Unit) {
    configList += block
  }

  override fun buildModels(modelBuilder: KiteEpoxyController.() -> Unit) {
    modelBuilderList += modelBuilder
  }

  fun create(): KiteEpoxyController {
    val isReadyList = isReadyList.toList()
    val isReady = { isReadyList.all { KiteEpoxyIsReadyScope().run(it) } }
    val configList = configList.toList()
    val config: KiteEpoxyController.() -> Unit = { configList.forEach { it.invoke(this) } }
    val modelBuilderList = modelBuilderList.toList()
    val builder: KiteEpoxyController.() -> Unit = { modelBuilderList.forEach { it.invoke(this) } }
    return KiteEpoxyController(
      isReady = isReady,
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
    controller.requestModelBuildIfReady()
  }
}
