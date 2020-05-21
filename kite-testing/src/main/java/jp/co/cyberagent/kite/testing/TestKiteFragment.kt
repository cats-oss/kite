package jp.co.cyberagent.kite.testing

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import java.util.concurrent.ConcurrentHashMap
import jp.co.cyberagent.kite.core.KiteDslScope
import jp.co.cyberagent.kite.runtime.KiteScopeModelFactory
import jp.co.cyberagent.kite.runtime.KiteScopeModelStoreOwner
import jp.co.cyberagent.kite.runtime.kiteDsl

class TestKiteFragment private constructor(
  @LayoutRes val contentLayoutId: Int
) : Fragment(contentLayoutId) {

  companion object {
    private val configMap = ConcurrentHashMap<Int, Config>()

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

  class Config(
    val kiteScopeModelFactory: KiteScopeModelFactory? = null,
    val storeOwner: StoreOwner = StoreOwner.SELF,
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
      scopeModelFactory = config.kiteScopeModelFactory
    ) {
      config.kiteDslBlock?.invoke(this, this@TestKiteFragment)
    }
  }
}
