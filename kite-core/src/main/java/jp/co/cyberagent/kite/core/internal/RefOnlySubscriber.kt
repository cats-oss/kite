package jp.co.cyberagent.kite.core.internal

internal class RefOnlySubscriber<T>(action: () -> T) : Subscriber<T>(action)
