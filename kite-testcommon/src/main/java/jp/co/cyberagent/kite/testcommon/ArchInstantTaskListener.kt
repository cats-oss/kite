package jp.co.cyberagent.kite.testcommon

import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec

object ArchInstantTaskListener : TestListener {
  override suspend fun beforeSpec(spec: Spec) {
    ArchTaskExecutor.getInstance().setDelegate(object : TaskExecutor() {
      override fun executeOnDiskIO(runnable: Runnable) = runnable.run()

      override fun postToMainThread(runnable: Runnable) = runnable.run()

      override fun isMainThread(): Boolean = true
    })
  }

  override suspend fun afterSpec(spec: Spec) {
    ArchTaskExecutor.getInstance().setDelegate(null)
  }
}
