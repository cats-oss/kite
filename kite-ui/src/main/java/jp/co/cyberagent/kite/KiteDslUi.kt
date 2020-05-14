package jp.co.cyberagent.kite

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

fun AppCompatActivity.activityKiteDsl(
  scopeModelFactory: ViewModelProvider.Factory? = null,
  body: KiteDslScope.() -> Unit
) {
  val factory = scopeModelFactory ?: defaultViewModelProviderFactory
  val scopeModel = factory.create(KiteComponentScopeModel::class.java)
  kiteDsl(this, scopeModel, body)
}

fun Fragment.fragmentKiteDsl(
  scopeModelFactory: ViewModelProvider.Factory? = null,
  body: KiteDslScope.() -> Unit
) {
  val factory = scopeModelFactory ?: defaultViewModelProviderFactory
  val scopeModel = factory.create(KiteComponentScopeModel::class.java)
  kiteDsl(viewLifecycleOwner, scopeModel, body)
}
