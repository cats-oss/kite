package jp.co.cyberagent.kite

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.observe

inline fun <reified T : KiteComponentScopeModel> AppCompatActivity.activityUi(
  noinline factoryProducer: () -> ViewModelProvider.Factory = { defaultViewModelProviderFactory },
  noinline body: KiteDslScope.() -> Unit
) {
  val holder = ViewModelLazy(T::class, { viewModelStore }, factoryProducer)
  kiteDsl(this, holder, body)
}

inline fun <reified T : KiteComponentScopeModel> Fragment.fragmentUi(
  noinline storeProducer: () -> ViewModelStore = { this.viewModelStore },
  noinline factoryProducer: () -> ViewModelProvider.Factory = { defaultViewModelProviderFactory },
  noinline body: KiteDslScope.() -> Unit
) {
  viewLifecycleOwnerLiveData.observe(this) { owner ->
    owner ?: return@observe
    val holder = ViewModelLazy(T::class, storeProducer, factoryProducer)
    kiteDsl(owner, holder, body)
  }
}
