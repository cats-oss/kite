package jp.co.cyberagent.kite.runtime

import android.app.Activity
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import jp.co.cyberagent.kite.core.KiteContext
import jp.co.cyberagent.kite.core.KiteDslScope
import jp.co.cyberagent.kite.core.plusAssign

fun ComponentActivity.kiteDsl(
  scopeModelFactory: KiteScopeModelFactory? = null,
  body: KiteDslScope.() -> Unit
) {
  val activity = this
  kiteDsl(this, this, scopeModelFactory) {
    ctx += activity as Activity
    ctx += activity as Context
    body.invoke(this)
  }
}

fun Fragment.kiteDsl(
  scopeModelStoreOwner: KiteScopeModelStoreOwner = this,
  scopeModelFactory: KiteScopeModelFactory? = null,
  body: KiteDslScope.() -> Unit
) {
  val fragment = this
  kiteDsl(viewLifecycleOwner, scopeModelStoreOwner, scopeModelFactory) {
    ctx += requireActivity() as Activity
    ctx += requireContext()
    ctx += fragment
    body.invoke(this)
  }
}

internal fun kiteDsl(
  lifecycleOwner: LifecycleOwner,
  scopeModelOwner: KiteScopeModelStoreOwner,
  scopeModelFactory: KiteScopeModelFactory? = null,
  body: KiteDslScope.() -> Unit
): KiteDslScope {
  val currentState = lifecycleOwner.lifecycle.currentState
  check(currentState == Lifecycle.State.INITIALIZED) {
    "Only can invoke kiteDsl when lifecycle is at the INITIALIZED state. " +
      "Current state is $currentState"
  }
  val scopeModel = ViewModelProvider(
    scopeModelOwner,
    scopeModelFactory ?: KiteScopeModelFactory()
  )[KiteScopeModel::class.java]
  return KiteDslScope(lifecycleOwner.lifecycleScope, KiteContext()).apply {
    scopeModel.addServiceToContext(ctx)
    ctx += lifecycleOwner
    ctx += scopeModel
    body.invoke(this)
  }
}
