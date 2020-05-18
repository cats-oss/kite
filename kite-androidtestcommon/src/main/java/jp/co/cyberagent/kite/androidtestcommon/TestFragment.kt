package jp.co.cyberagent.kite.androidtestcommon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class TestFragment : Fragment() {

  companion object {
    var onCreateAction: ((TestFragment) -> Unit)? = null
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return View(inflater.context)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    onCreateAction?.invoke(this)
    onCreateAction = null
  }
}
