package jp.co.cyberagent.kite

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

fun AppCompatActivity.kiteDsl(
  scopeModelFactory: ViewModelProvider.Factory? = null,
  body: KiteDslScope.() -> Unit
) {
  val scopeModel = ViewModelProvider(
    this,
    scopeModelFactory ?: defaultViewModelProviderFactory
  )[KiteComponentScopeModel::class.java]
  kiteDsl(this, scopeModel, body)
}

fun Fragment.kiteDsl(
  scopeModelOwner: ViewModelStoreOwner = this,
  scopeModelFactory: ViewModelProvider.Factory? = null,
  body: KiteDslScope.() -> Unit
) {
  val factory = scopeModelFactory
    ?: (scopeModelOwner as HasDefaultViewModelProviderFactory).defaultViewModelProviderFactory
  val scopeModel = ViewModelProvider(
    scopeModelOwner,
    scopeModelFactory ?: factory
  )[KiteComponentScopeModel::class.java]
  kiteDsl(viewLifecycleOwner, scopeModel, body)
}
