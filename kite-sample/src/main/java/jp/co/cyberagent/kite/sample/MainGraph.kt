package jp.co.cyberagent.kite.sample

object MainGraph {

  private var idCount = 1

  val id = idCount++

  object Dest {
    val home = idCount++
    val counterExample = idCount++
  }

  object Action {
    val toCounterExample = idCount++
  }
}
