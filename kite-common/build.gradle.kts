plugins {
  `module-config`
  id("com.android.library")
  kotlin("android")
  id("kotlin-android-extensions")
}

dependencies {
  implementation(Kotlin.stdlib.jdk8)
}
