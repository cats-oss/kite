package jp.co.cyberagent.kite.sample.timeline

import kotlinx.coroutines.delay
import java.text.DateFormat
import java.util.Date
import java.util.UUID
import kotlin.random.Random

class TimelineRepository {
  private val random = Random(0)

  suspend fun getTimeline(): List<Content> {
    randomDelay()
    return List(random.nextInt(20, 100)) {
      val text = "Content: Hello World! ${it * 1000}\nDateTime: ${DateFormat.getDateTimeInstance()
        .format(Date())}"
      Content(UUID.randomUUID().toString(), text)
    }
  }

  suspend fun checkFavorite(ids: List<String>): Map<String, Boolean> {
    randomDelay()
    return ids.associateWith { random.nextBoolean() }
  }

  suspend fun addFavorite(id: String) {
    randomDelay()
    if (random.nextInt(100) < 20) {
      error("$id Failure")
    }
  }

  suspend fun removeFavorite(id: String) {
    randomDelay()
    if (random.nextInt(100) < 20) {
      error("$id Failure")
    }
  }

  private suspend fun randomDelay() {
    delay(random.nextLong(300, 1500))
  }
}
