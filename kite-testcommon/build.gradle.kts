plugins {
  `module-config`
  id("com.android.library")
  kotlin("android")
  id("kotlin-android-extensions")
}

android {
  lintOptions {
    disable("RestrictedApi")
  }
}

dependencies {
  implementation(Kotlin.stdlib.jdk8)
  implementation(Kotest.runnerJunit5)
  implementation(Kotest.robolectric)
  implementation(AndroidX.appCompat)
  implementation(AndroidX.lifecycle.extensions)
}
