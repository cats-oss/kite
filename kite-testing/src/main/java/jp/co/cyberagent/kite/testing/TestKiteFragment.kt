package jp.co.cyberagent.kite.testing

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import java.util.concurrent.ConcurrentHashMap
import jp.co.cyberagent.kite.core.KiteContext
import jp.co.cyberagent.kite.core.KiteDslScope
import jp.co.cyberagent.kite.runtime.KiteScopeModelStoreOwner
import jp.co.cyberagent.kite.runtime.kiteDsl

/**
 * A helper Fragment for testing kite DSL in the instrumented test or Robolectric test.
 */
class TestKiteFragment private constructor(
  @LayoutRes val contentLayoutId: Int
) : Fragment(contentLayoutId) {

  companion object {
    private val configMap = ConcurrentHashMap<Int, Config>()

    /**
     * Creates a [FragmentFactory] that can instantiate this Fragment using [FragmentScenario].
     *
     * @param contentLayoutId set the fragment content with this layout id.
     * @param config setup kite DSL with this config in [onViewCreated].
     */
    @Suppress("KDocUnresolvedReference")
    fun makeFactory(
      @LayoutRes contentLayoutId: Int,
      config: Config
    ): FragmentFactory {
      configMap[contentLayoutId] = config
      return object : FragmentFactory() {
        override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
          return TestKiteFragment(contentLayoutId)
        }
      }
    }
  }

  /**
   * Defines how to setup kite DSL.
   *
   * @param storeOwner the owner of the state. The default value is the fragment itself.
   * @param kiteContext call [kiteDsl] with this context.
   * @param kiteDslBlock invoke this in the [KiteDslScope].
   */
  class Config(
    val storeOwner: StoreOwner = StoreOwner.SELF,
    val kiteContext: KiteContext = KiteContext(),
    val kiteDslBlock: (KiteDslScope.(Fragment) -> Unit)? = null
  ) {
    enum class StoreOwner {
      SELF,
      PARENT_ACTIVITY,
      PARENT_FRAGMENT
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val config = requireNotNull(configMap[contentLayoutId]) {
      val layoutName = resources.getResourceName(contentLayoutId)
      "KiteDsl config for layout $layoutName not found."
    }
    val scopeModelStoreOwner: KiteScopeModelStoreOwner = when (config.storeOwner) {
      Config.StoreOwner.SELF -> this
      Config.StoreOwner.PARENT_ACTIVITY -> requireActivity()
      Config.StoreOwner.PARENT_FRAGMENT -> requireParentFragment()
    }
    kiteDsl(
      scopeModelStoreOwner = scopeModelStoreOwner,
      kiteContext = config.kiteContext
    ) {
      config.kiteDslBlock?.invoke(this, this@TestKiteFragment)
    }
  }
}
