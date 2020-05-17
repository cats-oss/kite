package jp.co.cyberagent.kite

import androidx.lifecycle.ViewModelStore

class TestKiteScopeModelOwner : KiteScopeModelStoreOwner {

  private val store = ViewModelStore()

  override fun getViewModelStore(): ViewModelStore = store
}
