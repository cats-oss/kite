package jp.co.cyberagent.kite

interface KiteState

interface KiteGetter<T> : KiteState {
  val value: T
}

interface KiteProperty<T> : KiteGetter<T> {
  override var value: T
}
