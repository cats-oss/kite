package jp.co.cyberagent.kite.timeline

import com.google.common.truth.Truth
import io.mockk.coEvery
import io.mockk.mockk
import jp.co.cyberagent.kite.core.asKiteContextElement
import jp.co.cyberagent.kite.core.plusAssign
import jp.co.cyberagent.kite.sample.timeline.data.TimelineRepository
import jp.co.cyberagent.kite.sample.timeline.entity.Content
import jp.co.cyberagent.kite.sample.timeline.state.Timeline
import jp.co.cyberagent.kite.sample.timeline.state.TimelineState
import jp.co.cyberagent.kite.sample.timeline.state.useTimeline
import jp.co.cyberagent.kite.testing.runTestKiteDsl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test

@ExperimentalCoroutinesApi
class TestUseTimeline {

  private val repository = mockk<TimelineRepository>()

  @Test
  fun testGetTimeline() = runTestKiteDsl {
    kiteContext += repository.asKiteContextElement()
    val contents = listOf(Content("id", "text"))
    val favorite = mapOf("id" to true)
    val timeline = Timeline(contents, favorite)
    coEvery { repository.getTimelineContent() } returns contents
    coEvery { repository.checkFavorite(any()) } returns favorite

    val useCase = useTimeline()
    Truth.assertThat(useCase.timelineState.value).isEqualTo(TimelineState())
    useCase.fetchTimeline.invoke()
    Truth.assertThat(useCase.timelineState.value).isEqualTo(
      TimelineState(
        timeline = timeline,
        isLoading = false,
        error = null
      )
    )
  }
}
