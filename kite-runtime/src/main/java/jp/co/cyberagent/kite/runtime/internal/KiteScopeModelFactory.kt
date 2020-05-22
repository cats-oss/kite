package jp.co.cyberagent.kite.runtime.internal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * The factory to create a [KiteScopeModel].
 */
internal class KiteScopeModelFactory : ViewModelProvider.Factory {

  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    val scopeModel = KiteScopeModel()
    @Suppress("UNCHECKED_CAST")
    return scopeModel as T
  }
}
