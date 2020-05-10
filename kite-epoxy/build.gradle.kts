plugins {
  `module-config`
  id("com.android.library")
  kotlin("android")
  id("kotlin-android-extensions")
}

dependencies {
  implementation(project(":kite-core"))

  implementation(Kotlin.stdlib.jdk8)

  implementation(AndroidX.appCompat)
  implementation(AndroidX.coreKtx)

  api(Airbnb.epoxy)

  testImplementation(Testing.junit4)
}
