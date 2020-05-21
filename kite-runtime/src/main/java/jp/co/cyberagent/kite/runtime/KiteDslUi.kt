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
import jp.co.cyberagent.kite.core.KiteStateCreator
import jp.co.cyberagent.kite.core.MainThreadChecker
import jp.co.cyberagent.kite.core.plusAssign
import jp.co.cyberagent.kite.runtime.internal.AndroidMainThreadChecker
import jp.co.cyberagent.kite.runtime.internal.LiveDataBackedKiteStateCreator

fun ComponentActivity.kiteDsl(
  scopeModelFactory: KiteScopeModelFactory? = null,
  kiteContext: KiteContext = KiteContext(),
  block: KiteDslScope.() -> Unit
) {
  val activity = this
  kiteContext += activity as Activity
  kiteContext += activity as Context
  kiteDsl(this, this, scopeModelFactory, kiteContext, block)
}

fun Fragment.kiteDsl(
  scopeModelStoreOwner: KiteScopeModelStoreOwner = this,
  scopeModelFactory: KiteScopeModelFactory? = null,
  kiteContext: KiteContext = KiteContext(),
  block: KiteDslScope.() -> Unit
) {
  val fragment = this
  kiteContext += requireActivity() as Activity
  kiteContext += requireContext()
  kiteContext += fragment
  kiteDsl(viewLifecycleOwner, scopeModelStoreOwner, scopeModelFactory, kiteContext, block)
}

internal fun kiteDsl(
  lifecycleOwner: LifecycleOwner,
  scopeModelOwner: KiteScopeModelStoreOwner,
  scopeModelFactory: KiteScopeModelFactory? = null,
  kiteContext: KiteContext = KiteContext(),
  block: KiteDslScope.() -> Unit
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
  kiteContext += LiveDataBackedKiteStateCreator(kiteContext) as KiteStateCreator
  kiteContext += lifecycleOwner
  kiteContext += scopeModel
  kiteContext += AndroidMainThreadChecker() as MainThreadChecker
  scopeModel.addServiceToContext(kiteContext)
  return KiteDslScope(lifecycleOwner.lifecycleScope, kiteContext).apply(block)
}
