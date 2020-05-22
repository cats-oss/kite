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
import jp.co.cyberagent.kite.runtime.internal.KiteScopeModel
import jp.co.cyberagent.kite.runtime.internal.KiteScopeModelFactory
import jp.co.cyberagent.kite.runtime.internal.LiveDataBackedKiteStateCreator

/**
 * Creates a [KiteDslScope] and calls the specified kite DSL block with this scope.
 * Can only invoke this function inside [Activity.onCreate], otherwise [IllegalStateException]
 * will be thrown.
 *
 * Several element will be set into the [KiteContext] of this scope:
 *
 * - Activity
 * - Context
 *
 * These elements can be retrieved via their type as the key.
 *
 * @param kiteContext additional to context of the the scope.
 * @param block the DSL which will be invoked in the scope.
 */
fun ComponentActivity.kiteDsl(
  kiteContext: KiteContext = KiteContext(),
  block: KiteDslScope.() -> Unit
) {
  val activity = this
  val mergedContext = kiteContext + kiteContextOf(
    activity.asKiteContextElement<Activity>(),
    activity.asKiteContextElement<Context>()
  )
  kiteDsl(this, this, mergedContext, block)
}

/**
 * Creates a [KiteDslScope] and calls the specified kite DSL block with this scope.
 * Can only invoke this function inside [Fragment.onViewCreated], otherwise [IllegalStateException]
 * will be thrown.
 *
 * Several element will be set into the [KiteContext] of the scope:
 *
 * - Activity
 * - Context
 * - Fragment
 *
 * These elements can be retrieved via their type as the key.
 *
 * @param scopeModelStoreOwner the scope of the [KiteScopeModel]. The default value if the fragment itself.
 * @param kiteContext additional to context of the the scope.
 * @param block the DSL which will be invoked in the scope.
 */
fun Fragment.kiteDsl(
  scopeModelStoreOwner: KiteScopeModelStoreOwner = this,
  kiteContext: KiteContext = KiteContext(),
  block: KiteDslScope.() -> Unit
) {
  val fragment = this
  val mergedContext = kiteContext + kiteContextOf(
    requireActivity().asKiteContextElement<Activity>(),
    requireContext().asKiteContextElement(),
    fragment.asKiteContextElement()
  )
  kiteDsl(viewLifecycleOwner, scopeModelStoreOwner, mergedContext, block)
}

internal fun kiteDsl(
  lifecycleOwner: LifecycleOwner,
  scopeModelOwner: KiteScopeModelStoreOwner,
  kiteContext: KiteContext = KiteContext(),
  block: KiteDslScope.() -> Unit
): KiteDslScope {
  val currentState = lifecycleOwner.lifecycle.currentState
  check(currentState == Lifecycle.State.INITIALIZED) {
    "Only can invoke kiteDsl when lifecycle is at the INITIALIZED state. " +
      "Current state is $currentState"
  }
  val scopeModelFactory = KiteScopeModelFactory()
  val scopeModel = ViewModelProvider(
    scopeModelOwner,
    scopeModelFactory
  )[KiteScopeModel::class.java]
  // Since kiteContext is provided internally, we modified it directly here.
  val stateCreator: KiteStateCreator = LiveDataBackedKiteStateCreator(kiteContext)
  val mainThreadChecker: MainThreadChecker = AndroidMainThreadChecker()
  kiteContext.setByType(stateCreator)
  kiteContext.setByType(mainThreadChecker)
  kiteContext.setByType(lifecycleOwner)
  kiteContext.setByType(scopeModel)
  return KiteDslScope(lifecycleOwner.lifecycleScope, kiteContext).apply(block)
}
