package jp.co.cyberagent.kite.runtime

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import jp.co.cyberagent.kite.runtime.internal.KiteScopeModel

class TestKiteViewModelProvider : ViewModelProvider(
  object : ViewModelStoreOwner {
    private val store = ViewModelStore()
    override fun getViewModelStore(): ViewModelStore = store
  },
  object : Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
      @Suppress("UNCHECKED_CAST")
      return KiteScopeModel(SavedStateHandle()) as T
    }
  }
)
