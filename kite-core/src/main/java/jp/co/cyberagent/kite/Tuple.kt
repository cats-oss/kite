package jp.co.cyberagent.kite

typealias Tuple2<A, B> = Pair<A, B>

data class Tuple3<out A, out B, out C>(val a: A, val b: B, val c: C)

data class Tuple4<out A, out B, out C, out D>(val a: A, val b: B, val c: C, val d: D)

data class Tuple5<out A, out B, out C, out D, out E>(
  val a: A,
  val b: B,
  val c: C,
  val d: D,
  val e: E
)

data class Tuple6<out A, out B, out C, out D, out E, out F>(
  val a: A,
  val b: B,
  val c: C,
  val d: D,
  val e: E,
  val f: F
)
