package jp.co.cyberagent.kite.sample.timeline.data

import jp.co.cyberagent.kite.sample.timeline.entity.Content
import kotlinx.coroutines.delay
import java.text.DateFormat.getDateTimeInstance
import java.util.Date
import java.util.UUID
import kotlin.random.Random

class TimelineRepository {

  private val random = Random(0)

  private val contents = mutableListOf<Content>()
  private val isFavorite = mutableMapOf<String, Boolean>()

  suspend fun getTimelineContent(): List<Content> {
    randomDelay()
    randomError("Get Timeline Fail")
    val newContents = List(random.nextInt(1, 10)) {
      val text =
        "Content: Hello World!\nDateTime: ${getDateTimeInstance().format(Date())}"
      Content(
        UUID.randomUUID().toString(), text
      )
    }
    contents.addAll(0, newContents)
    return contents.toList()
  }

  suspend fun checkFavorite(ids: List<String>): Map<String, Boolean> {
    randomDelay()
    randomError("Check Favorite Fail")
    val newIds = ids - isFavorite.keys
    isFavorite += newIds.associateWith { random.nextBoolean() }
    return isFavorite.toMap()
  }

  suspend fun addFavorite(id: String) {
    randomDelay()
    randomError("Add Favorite Fail")
    isFavorite += id to true
  }

  suspend fun removeFavorite(id: String) {
    randomDelay()
    randomError("Remove Favorite Fail")
    isFavorite += id to false
  }

  private suspend fun randomDelay() {
    delay(random.nextLong(300, 1500))
  }

  private fun randomError(message: String) {
    if (random.nextInt(100) < 20) {
      error(message)
    }
  }
}
