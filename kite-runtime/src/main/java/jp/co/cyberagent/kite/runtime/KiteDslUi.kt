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
import jp.co.cyberagent.kite.core.asKiteContextElement
import jp.co.cyberagent.kite.core.kiteContextOf
import jp.co.cyberagent.kite.core.setByType
import jp.co.cyberagent.kite.runtime.internal.AndroidMainThreadChecker
import jp.co.cyberagent.kite.runtime.internal.LiveDataBackedKiteStateCreator

fun ComponentActivity.kiteDsl(
  scopeModelFactory: KiteScopeModelFactory? = null,
  kiteContext: KiteContext = KiteContext(),
  block: KiteDslScope.() -> Unit
) {
  val activity = this
  val mergedContext = kiteContext + kiteContextOf(
    activity.asKiteContextElement<Activity>(),
    activity.asKiteContextElement<Context>()
  )
  kiteDsl(this, this, scopeModelFactory, mergedContext, block)
}

fun Fragment.kiteDsl(
  scopeModelStoreOwner: KiteScopeModelStoreOwner = this,
  scopeModelFactory: KiteScopeModelFactory? = null,
  kiteContext: KiteContext = KiteContext(),
  block: KiteDslScope.() -> Unit
) {
  val fragment = this
  val mergedContext = kiteContext + kiteContextOf(
    requireActivity().asKiteContextElement<Activity>(),
    requireContext().asKiteContextElement(),
    fragment.asKiteContextElement()
  )
  kiteDsl(viewLifecycleOwner, scopeModelStoreOwner, scopeModelFactory, mergedContext, block)
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
  // Since kiteContext is provided internally, we modified it directly here.
  val stateCreator: KiteStateCreator = LiveDataBackedKiteStateCreator(kiteContext)
  val mainThreadChecker: MainThreadChecker = AndroidMainThreadChecker()
  kiteContext.setByType(stateCreator)
  kiteContext.setByType(mainThreadChecker)
  kiteContext.setByType(lifecycleOwner)
  kiteContext.setByType(scopeModel)
  scopeModel.addServiceToContext(kiteContext)
  return KiteDslScope(lifecycleOwner.lifecycleScope, kiteContext).apply(block)
}
