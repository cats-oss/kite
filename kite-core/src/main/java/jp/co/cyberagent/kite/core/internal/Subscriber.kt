package jp.co.cyberagent.kite.core.internal

internal open class Subscriber<T>(
  private val action: () -> T
) {
  operator fun invoke(): T = action.invoke()
}
