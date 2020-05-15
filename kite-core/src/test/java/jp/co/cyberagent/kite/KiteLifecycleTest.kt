package jp.co.cyberagent.kite

import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.Lifecycle.State
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.experimental.robolectric.RobolectricTest
import io.kotest.matchers.shouldBe
import io.mockk.spyk
import io.mockk.verify

@RobolectricTest
class KiteLifecycleTest : StringSpec({

  "When state change should invoke corresponding onXXX" {
    val owner = TestLifecycleOwner()
    val kiteScopeModel = KiteScopeModel()
    val kite = KiteDslScopeImpl(owner, kiteScopeModel)
    val callback = spyk<() -> Unit>()
    var cnt = 0

    forAll(
      row<Event, KiteDslScope.() -> Unit>(
        Event.ON_CREATE,
        {
          onCreate {
            currentState shouldBe State.CREATED
            callback.invoke()
          }
        }
      ),
      row<Event, KiteDslScope.() -> Unit>(
        Event.ON_START,
        {
          onStart {
            currentState shouldBe State.STARTED
            callback.invoke()
          }
        }
      ),
      row<Event, KiteDslScope.() -> Unit>(
        Event.ON_RESUME,
        {
          onResume {
            currentState shouldBe State.RESUMED
            callback.invoke()
          }
        }
      ),
      row<Event, KiteDslScope.() -> Unit>(
        Event.ON_PAUSE,
        {
          onPause {
            currentState shouldBe State.STARTED
            callback.invoke()
          }
        }
      ),
      row<Event, KiteDslScope.() -> Unit>(
        Event.ON_STOP,
        {
          onStop {
            currentState shouldBe State.CREATED
            callback.invoke()
          }
        }
      ),
      row<Event, KiteDslScope.() -> Unit>(
        Event.ON_DESTROY,
        {
          onDestroy {
            currentState shouldBe State.DESTROYED
            callback.invoke()
          }
        }
      )
    ) { event, block ->
      kite.apply(block)
      owner.lifecycle.handleLifecycleEvent(event)
      cnt++
      verify(exactly = cnt) { callback.invoke() }
    }
  }
})
