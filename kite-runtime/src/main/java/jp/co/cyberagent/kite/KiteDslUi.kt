package jp.co.cyberagent.kite

import android.app.Activity
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

fun ComponentActivity.kiteDsl(
  scopeModelFactory: KiteScopeModelFactory? = null,
  body: KiteDslScope.() -> Unit
) {
  val scopeModel = ViewModelProvider(
    this,
    scopeModelFactory ?: SimpleKiteScopeModelFactory()
  )[KiteScopeModel::class.java]
  kiteDsl(this, scopeModel) {
    setContextualValueIfAbsent<Activity> { this@kiteDsl }
    setContextualValueIfAbsent<Context> { this@kiteDsl }
    body.invoke(this)
  }
}

fun Fragment.kiteDsl(
  scopeModelOwner: KiteScopeModelStoreOwner = this,
  scopeModelFactory: KiteScopeModelFactory? = null,
  body: KiteDslScope.() -> Unit
) {
  val scopeModel = ViewModelProvider(
    scopeModelOwner,
    scopeModelFactory ?: SimpleKiteScopeModelFactory()
  )[KiteScopeModel::class.java]
  kiteDsl(viewLifecycleOwner, scopeModel) {
    setContextualValueIfAbsent<Activity> { requireActivity() }
    setContextualValueIfAbsent { requireContext() }
    setContextualValueIfAbsent { this@kiteDsl }
    body.invoke(this)
  }
}
