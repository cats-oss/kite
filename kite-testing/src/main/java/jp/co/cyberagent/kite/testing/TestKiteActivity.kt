package jp.co.cyberagent.kite.testing

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import java.util.concurrent.ConcurrentHashMap
import jp.co.cyberagent.kite.KiteDslScope
import jp.co.cyberagent.kite.KiteScopeModelFactory
import jp.co.cyberagent.kite.kiteDsl

class TestKiteActivity : AppCompatActivity() {

  companion object {
    private val configMap = ConcurrentHashMap<Int, Config>()
    private const val KEY_CONTENT_LAYOUT_ID = "CONTENT_LAYOUT_ID"

    fun makeIntent(
      @LayoutRes contentLayoutId: Int,
      config: Config
    ): Intent {
      configMap[contentLayoutId] = config
      val intent = Intent.makeMainActivity(
        ComponentName(getInstrumentation().targetContext, TestKiteActivity::class.java)
      )
      val resolvableIntent = if (getInstrumentation().targetContext.packageManager.resolveActivity(
        intent,
        0
      ) != null
      ) intent else Intent.makeMainActivity(
        ComponentName(getInstrumentation().context, TestKiteActivity::class.java)
      )
      resolvableIntent.putExtra(KEY_CONTENT_LAYOUT_ID, contentLayoutId)
      return resolvableIntent
    }
  }

  class Config(
    var kiteScopeModelFactory: KiteScopeModelFactory? = null,
    var kiteDslBody: (KiteDslScope.() -> Unit)? = null
  )

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val contentLayoutId = intent.getIntExtra(KEY_CONTENT_LAYOUT_ID, 0)
    val config = requireNotNull(configMap[contentLayoutId]) {
      val layoutName = resources.getResourceName(contentLayoutId)
      "KiteDsl config for layout $layoutName not found."
    }
    kiteDsl(scopeModelFactory = config.kiteScopeModelFactory) {
      config.kiteDslBody?.invoke(this)
    }
  }
}
