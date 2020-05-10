package jp.co.cyberagent.kite

typealias Tuple2<A, B> = Pair<A, B>

data class Tuple3<A, B, C>(val a: A, val b: B, val c: C)

data class Tuple4<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)

data class Tuple5<A, B, C, D, E>(val a: A, val b: B, val c: C, val d: D, val e: E)

data class Tuple6<A, B, C, D, E, F>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F)
