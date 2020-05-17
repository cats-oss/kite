package jp.co.cyberagent.kite

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class TestActivity : AppCompatActivity() {

  companion object {
    var onCreateAction: ((TestActivity) -> Unit)? = null
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    onCreateAction?.invoke(this)
    onCreateAction = null
  }
}
