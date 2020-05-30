package jp.co.cyberagent.kite.runtime

import android.app.Activity
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import jp.co.cyberagent.kite.core.KiteContext
import jp.co.cyberagent.kite.core.KiteDslScope
import jp.co.cyberagent.kite.core.KiteStateCreator
import jp.co.cyberagent.kite.core.MainThreadChecker
import jp.co.cyberagent.kite.core.buildKiteContext
import jp.co.cyberagent.kite.core.setByType
import jp.co.cyberagent.kite.runtime.internal.AndroidMainThreadChecker
import jp.co.cyberagent.kite.runtime.internal.KiteScopeModel
import jp.co.cyberagent.kite.runtime.internal.LiveDataBackedKiteStateCreator

/**
 * Creates a [KiteDslScope] and calls the specified kite DSL block with this scope.
 * Can only invoke this function inside [Activity.onCreate], otherwise [IllegalStateException]
 * will be thrown.
 *
 * Several element will be set into the [KiteContext] of this scope:
 *
 * - Context
 * - Activity
 * - LifecycleOwner
 * - SaveStateHandle
 *
 * These elements can be retrieved via their type as the key.
 *
 * @param kiteContext additional to context of the the scope.
 * @param block the DSL that will be invoked in the scope.
 */
fun ComponentActivity.kiteDsl(
  kiteContext: KiteContext = KiteContext(),
  block: KiteDslScope.() -> Unit
) {
  val activity = this
  val mergedContext = buildKiteContext {
    setByType<Activity>(activity)
    setByType<Context>(activity)
  } + kiteContext
  val viewModelProvider = ViewModelProvider(this, defaultViewModelProviderFactory)
  kiteDsl(this, viewModelProvider, mergedContext, block)
}

/**
 * Creates a [KiteDslScope] and calls the specified kite DSL block with this scope.
 * Can only invoke this function inside [Fragment.onViewCreated], otherwise [IllegalStateException]
 * will be thrown.
 *
 * Several element will be set into the [KiteContext] of the scope:
 *
 * - Context
 * - Fragment
 * - Activity
 * - LifecycleOwner
 * - SaveStateHandle
 *
 * These elements can be retrieved via their type as the key.
 *
 * @param scopeModelStoreOwner the scope of the [KiteScopeModel]. The default value if the fragment itself.
 * @param kiteContext additional to context of the the scope.
 * @param block the DSL that will be invoked in the scope.
 */
fun Fragment.kiteDsl(
  scopeModelStoreOwner: KiteScopeModelStoreOwner = this,
  kiteContext: KiteContext = KiteContext(),
  block: KiteDslScope.() -> Unit
) {
  val fragment = this
  val mergedContext = buildKiteContext {
    setByType<Activity>(requireActivity())
    setByType(requireContext())
    setByType(fragment)
  } + kiteContext
  val viewModelProvider = if (scopeModelStoreOwner == requireActivity()) {
    ViewModelProvider(requireActivity(), requireActivity().defaultViewModelProviderFactory)
  } else {
    ViewModelProvider(scopeModelStoreOwner, defaultViewModelProviderFactory)
  }
  kiteDsl(viewLifecycleOwner, viewModelProvider, mergedContext, block)
}

@VisibleForTesting
internal fun kiteDsl(
  lifecycleOwner: LifecycleOwner,
  viewModelProvider: ViewModelProvider,
  kiteContext: KiteContext = KiteContext(),
  block: KiteDslScope.() -> Unit
): KiteDslScope {
  val currentState = lifecycleOwner.lifecycle.currentState
  check(currentState == Lifecycle.State.INITIALIZED) {
    "Only can invoke kiteDsl when lifecycle is at the INITIALIZED state. " +
      "Current state is $currentState"
  }
  val scopeModel = viewModelProvider[KiteScopeModel::class.java]
  val mainThreadChecker: MainThreadChecker = AndroidMainThreadChecker()
  val stateCreator: KiteStateCreator = LiveDataBackedKiteStateCreator(
    lifecycleOwner = lifecycleOwner,
    scopeModel = scopeModel,
    mainThreadChecker = mainThreadChecker
  )
  val mergedKiteContext = buildKiteContext {
    setByType(mainThreadChecker)
    setByType(lifecycleOwner)
    setByType(stateCreator)
    setByType(scopeModel.savedStateHandle)
  } + kiteContext
  return KiteDslScope(lifecycleOwner.lifecycleScope, mergedKiteContext).apply(block)
}
