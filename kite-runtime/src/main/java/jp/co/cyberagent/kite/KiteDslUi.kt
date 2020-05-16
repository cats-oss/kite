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
  val activity = this
  kiteDsl(this, scopeModel) {
    ctx += activity as Activity
    ctx += activity as Context
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
  val fragment = this
  kiteDsl(viewLifecycleOwner, scopeModel) {
    ctx += requireActivity() as Activity
    ctx += requireContext()
    ctx += fragment
    body.invoke(this)
  }
}
