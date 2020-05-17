package jp.co.cyberagent.kite

import android.app.Activity
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope

fun ComponentActivity.kiteDsl(
  scopeModelFactory: KiteScopeModelFactory? = null,
  body: KiteDslScope.() -> Unit
) {
  val scopeModel = ViewModelProvider(
    this,
    scopeModelFactory ?: KiteScopeModelFactory()
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
    scopeModelFactory ?: KiteScopeModelFactory()
  )[KiteScopeModel::class.java]
  val fragment = this
  kiteDsl(viewLifecycleOwner, scopeModel) {
    ctx += requireActivity() as Activity
    ctx += requireContext()
    ctx += fragment
    body.invoke(this)
  }
}

internal fun kiteDsl(
  lifecycleOwner: LifecycleOwner,
  scopeModel: KiteScopeModel,
  body: KiteDslScope.() -> Unit
): KiteDslScope {
  val currentState = lifecycleOwner.lifecycle.currentState
  check(currentState == Lifecycle.State.INITIALIZED) {
    "Only can invoke kiteDsl when lifecycle is at the INITIALIZED state. " +
      "Current state is $currentState"
  }
  return KiteDslScope(lifecycleOwner.lifecycleScope, KiteContext()).apply {
    scopeModel.addServiceToContext(ctx)
    ctx += lifecycleOwner
    ctx += scopeModel
    body.invoke(this)
  }
}
