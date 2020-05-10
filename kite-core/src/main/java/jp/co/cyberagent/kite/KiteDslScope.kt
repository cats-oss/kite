package jp.co.cyberagent.kite

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@KiteDslMaker
class KiteDslScope(
  val lifecycleOwner: LifecycleOwner,
  stateHolderLazy: Lazy<KiteComponentScopeModel>
) {

  val componentScopeModel: KiteComponentScopeModel by stateHolderLazy

  val stateDependencyManager = KiteStateDependencyManager()

  fun launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
  ) = lifecycleOwner.lifecycleScope.launch(context, start, block)
}

fun dslUi(
  lifecycleOwner: LifecycleOwner,
  stateHolderLazy: Lazy<KiteComponentScopeModel>,
  body: KiteDslScope.() -> Unit
) {
  KiteDslScope(lifecycleOwner, stateHolderLazy).apply {
    componentScopeModel.registerIn(this)
    onCreate { body.invoke(this) }
  }
}

inline fun <reified T : KiteComponentScopeModel> AppCompatActivity.activityUi(
  noinline factoryProducer: () -> ViewModelProvider.Factory = { defaultViewModelProviderFactory },
  noinline body: KiteDslScope.() -> Unit
) {
  val holder = ViewModelLazy(T::class, { viewModelStore }, factoryProducer)
  dslUi(this, holder, body)
}

inline fun <reified T : KiteComponentScopeModel> Fragment.fragmentUi(
  noinline storeProducer: () -> ViewModelStore = { this.viewModelStore },
  noinline factoryProducer: () -> ViewModelProvider.Factory = { defaultViewModelProviderFactory },
  noinline body: KiteDslScope.() -> Unit
) {
  viewLifecycleOwnerLiveData.observe(this) { owner ->
    owner ?: return@observe
    val holder = ViewModelLazy(T::class, storeProducer, factoryProducer)
    dslUi(owner, holder, body)
  }
}
