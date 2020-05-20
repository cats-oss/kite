plugins {
  `module-config`
  id("java-library")
  kotlin("jvm")
}

dependencies {
  implementation(Kotlin.stdlib.jdk8)
  api(Kotest.runnerJunit5)
  api(Kotest.assertionsCore)
  api(Kotest.property)
}
