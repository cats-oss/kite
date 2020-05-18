plugins {
  `module-config`
  id("java-library")
  kotlin("jvm")
}

dependencies {
  implementation(project(":kite-common"))
  implementation(Kotlin.stdlib.jdk8)
  api(KotlinX.coroutines.core)

  testImplementation(project(":kite-testcommon"))
}
