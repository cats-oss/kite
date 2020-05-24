package jp.co.cyberagent.kite.epoxy.paging

import android.os.Handler
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModel
import jp.co.cyberagent.kite.core.KiteDslScope
import jp.co.cyberagent.kite.core.KiteState
import jp.co.cyberagent.kite.core.MainThreadChecker
import jp.co.cyberagent.kite.core.checkIsMainThread
import jp.co.cyberagent.kite.core.requireByType
import jp.co.cyberagent.kite.core.subscribe
import jp.co.cyberagent.kite.epoxy.EpoxyConfig
import jp.co.cyberagent.kite.epoxy.EpoxyModelBuilder
import jp.co.cyberagent.kite.epoxy.KiteEpoxyBuildModelScope
import jp.co.cyberagent.kite.epoxy.KiteEpoxyConfigureControllerScope
import jp.co.cyberagent.kite.epoxy.KiteEpoxyDslScope

interface KiteEpoxyPagingDslScope<T> : KiteEpoxyDslScope {

  fun transformer(f: (Int, T?) -> EpoxyModel<*>)

  fun buildPagingModels(modelBuilder: EpoxyPagingModelBuilder)

  fun KiteState<*>.needForceRebuild()
}

private typealias EpoxyPagingModelBuilder =
  KiteEpoxyBuildModelScope.(EpoxyController, List<EpoxyModel<*>>) -> Unit

private class KiteEpoxyPagingDslScopeImpl<T> : KiteEpoxyPagingDslScope<T> {

  private val configList = mutableListOf<EpoxyConfig>()

  private val modelBuilderList = mutableListOf<EpoxyPagingModelBuilder>()

  private lateinit var transformer: (Int, T?) -> EpoxyModel<*>

  val needForceRebuildStateList = mutableListOf<KiteState<*>>()

  var modelBuildingHandler: Handler = EpoxyController.defaultModelBuildingHandler

  var diffingHandler: Handler = EpoxyController.defaultDiffingHandler

  override fun config(block: EpoxyConfig) {
    configList += block
  }

  override fun buildModels(modelBuilder: EpoxyModelBuilder) {
    val builder: EpoxyPagingModelBuilder = { controller, _ ->
      modelBuilder.invoke(this, controller)
    }
    modelBuilderList += builder
  }

  override fun buildPagingModels(modelBuilder: EpoxyPagingModelBuilder) {
    modelBuilderList += modelBuilder
  }

  override fun transformer(f: (Int, T?) -> EpoxyModel<*>) {
    transformer = f
  }

  override fun KiteState<*>.needForceRebuild() {
    needForceRebuildStateList += this
  }

  fun create(): KitePagedListEpoxyController<T> {
    val configList = configList.toList()
    val config: EpoxyController.() -> Unit = {
      configList.forEach {
        it.invoke(KiteEpoxyConfigureControllerScope(), this)
      }
    }
    val modelBuilderList = modelBuilderList.toList()
    val builder: EpoxyController.(List<EpoxyModel<*>>) -> Unit = { models ->
      modelBuilderList.forEach {
        it.invoke(KiteEpoxyBuildModelScope(), this, models)
      }
    }
    return KitePagedListEpoxyController(
      config = config,
      transformer = transformer,
      modelBuilder = builder,
      modelBuildingHandler = modelBuildingHandler,
      diffingHandler = diffingHandler
    )
  }
}

fun <T> KiteDslScope.epoxyPagingDsl(
  recyclerView: RecyclerView,
  pagedList: KiteState<PagedList<T>?>,
  block: KiteEpoxyDslScope.() -> Unit
) {
  kiteContext.requireByType<MainThreadChecker>().checkIsMainThread("epoxyPagingDsl")
  val scope = KiteEpoxyPagingDslScopeImpl<T>().apply(block)
  val controller = scope.create()
  recyclerView.adapter = controller.adapter
  val needForceStates = scope.needForceRebuildStateList.toList()
  subscribe {
    for (state in needForceStates) {
      /* no op, only for subscription */
      state.value
    }
    controller.force = true
  }
  subscribe {
    controller.submitList(pagedList.value)
  }
  subscribe {
    controller.requestModelBuild()
  }
}
