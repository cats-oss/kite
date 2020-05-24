package jp.co.cyberagent.kite.testing

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import java.util.concurrent.ConcurrentHashMap
import jp.co.cyberagent.kite.core.KiteContext
import jp.co.cyberagent.kite.core.KiteDslScope
import jp.co.cyberagent.kite.runtime.kiteDsl

/**
 * A helper Activity for testing kite DSL in the instrumented test or Robolectric test.
 */
class TestKiteActivity : AppCompatActivity() {

  companion object {
    private val configMap = ConcurrentHashMap<Int, Config>()
    private const val KEY_CONTENT_LAYOUT_ID = "CONTENT_LAYOUT_ID"

    /**
     * Creates an [Intent] that can launch into this Activity using [ActivityScenario]
     * with specific configuration.
     *
     * @param contentLayoutId set the activity content with this layout id.
     * @param config setup kite DSL with this config in [onCreate].
     */
    fun makeIntent(
      @LayoutRes contentLayoutId: Int,
      config: Config
    ): Intent {
      configMap[contentLayoutId] = config
      val intent = Intent.makeMainActivity(
        ComponentName(getInstrumentation().targetContext, TestKiteActivity::class.java)
      )
      val packageManager = getInstrumentation().targetContext.packageManager
      val resolvableIntent = if (packageManager.resolveActivity(intent, 0) != null) {
        intent
      } else {
        Intent.makeMainActivity(
          ComponentName(getInstrumentation().context, TestKiteActivity::class.java)
        )
      }
      resolvableIntent.putExtra(KEY_CONTENT_LAYOUT_ID, contentLayoutId)
      return resolvableIntent
    }
  }

  /**
   * Defines how to setup kite DSL.
   *
   * @param kiteContext call [kiteDsl] with this context.
   * @param kiteDslBlock invoke this in the [KiteDslScope].
   */
  class Config(
    val kiteContext: KiteContext = KiteContext(),
    var kiteDslBlock: (KiteDslScope.(Activity) -> Unit)? = null
  )

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val contentLayoutId = intent.getIntExtra(KEY_CONTENT_LAYOUT_ID, 0)
    setContentView(contentLayoutId)
    val config = requireNotNull(configMap[contentLayoutId]) {
      val layoutName = resources.getResourceName(contentLayoutId)
      "KiteDsl config for layout $layoutName not found."
    }
    kiteDsl(kiteContext = config.kiteContext) {
      config.kiteDslBlock?.invoke(this, this@TestKiteActivity)
    }
  }
}
