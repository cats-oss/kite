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
  api(project(":kite-testcommon"))
  implementation(Kotlin.stdlib.jdk8)
  implementation(AndroidX.appCompat)
  implementation(AndroidX.lifecycle.extensions)

  api(Kotest.robolectric)
}
