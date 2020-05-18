package jp.co.cyberagent.kite.testcommon

import io.kotlintest.Spec
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun <T : Any> Spec.memoize(init: () -> T): ReadOnlyProperty<Nothing?, T> {
  var value: T? = null

  beforeTest {
    value = init()
  }

  afterTest {
    value = null
  }

  return object : ReadOnlyProperty<Nothing?, T> {
    override fun getValue(thisRef: Nothing?, property: KProperty<*>): T {
      return value!!
    }
  }
}
