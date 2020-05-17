package jp.co.cyberagent.kite

import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.Lifecycle.State
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.experimental.robolectric.RobolectricTest
import io.kotest.matchers.shouldBe

@RobolectricTest
class KiteLifecycleTest : StringSpec({

  "When state change should invoke corresponding onXXX" {
    val owner = TestLifecycleOwner()
    val kite = kiteDsl(owner, TestKiteScopeModelOwner()) { /* no op */ }
    var invoked: Boolean

    forAll(
      row<Event, KiteDslScope.() -> Unit>(
        Event.ON_CREATE,
        {
          onCreate {
            currentState shouldBe State.CREATED
            invoked = true
          }
        }
      ),
      row<Event, KiteDslScope.() -> Unit>(
        Event.ON_START,
        {
          onStart {
            currentState shouldBe State.STARTED
            invoked = true
          }
        }
      ),
      row<Event, KiteDslScope.() -> Unit>(
        Event.ON_RESUME,
        {
          onResume {
            currentState shouldBe State.RESUMED
            invoked = true
          }
        }
      ),
      row<Event, KiteDslScope.() -> Unit>(
        Event.ON_PAUSE,
        {
          onPause {
            currentState shouldBe State.STARTED
            invoked = true
          }
        }
      ),
      row<Event, KiteDslScope.() -> Unit>(
        Event.ON_STOP,
        {
          onStop {
            currentState shouldBe State.CREATED
            invoked = true
          }
        }
      ),
      row<Event, KiteDslScope.() -> Unit>(
        Event.ON_DESTROY,
        {
          onDestroy {
            currentState shouldBe State.DESTROYED
            invoked = true
          }
        }
      )
    ) { event, block ->
      invoked = false
      kite.apply(block)
      owner.lifecycle.handleLifecycleEvent(event)
      invoked shouldBe true
    }
  }
})
