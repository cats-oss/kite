package jp.co.cyberagent.kite

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

inline fun <reified T : KiteComponentScopeModel> AppCompatActivity.activityKiteDsl(
  scopeModelFactory: ViewModelProvider.Factory? = null,
  noinline body: KiteDslScope.() -> Unit
) {
  val factory = scopeModelFactory ?: defaultViewModelProviderFactory
  val scopeModel = factory.create(T::class.java)
  kiteDsl(this, scopeModel, body)
}

inline fun <reified T : KiteComponentScopeModel> Fragment.fragmentKiteDsl(
  scopeModelFactory: ViewModelProvider.Factory? = null,
  noinline body: KiteDslScope.() -> Unit
) {
  val factory = scopeModelFactory ?: defaultViewModelProviderFactory
  val scopeModel = factory.create(T::class.java)
  kiteDsl(viewLifecycleOwner, scopeModel, body)
}
