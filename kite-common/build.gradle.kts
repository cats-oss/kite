plugins {
  `module-config`
  id("java-library")
  kotlin("jvm")
}

dependencies {
  implementation(Kotlin.stdlib.jdk8)
}
