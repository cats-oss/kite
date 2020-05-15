package jp.co.cyberagent.kite

import android.app.Activity
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

fun ComponentActivity.kiteDsl(
  scopeModelFactory: ViewModelProvider.Factory? = null,
  body: KiteDslScope.() -> Unit
) {
  val scopeModel = ViewModelProvider(
    this,
    scopeModelFactory ?: defaultViewModelProviderFactory
  )[KiteScopeModel::class.java]
  kiteDsl(this, scopeModel) {
    setContextualValueIfAbsent<Activity> { this@kiteDsl }
    setContextualValueIfAbsent<Context> { this@kiteDsl }
    body.invoke(this)
  }
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
  )[KiteScopeModel::class.java]
  kiteDsl(viewLifecycleOwner, scopeModel) {
    setContextualValueIfAbsent<Activity> { requireActivity() }
    setContextualValueIfAbsent { requireContext() }
    setContextualValueIfAbsent { this@kiteDsl }
    body.invoke(this)
  }
}
