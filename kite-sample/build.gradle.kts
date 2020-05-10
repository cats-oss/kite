@file:Suppress("UnstableApiUsage")

plugins {
  `module-config`
  id("com.android.application")
  kotlin("android")
  id("kotlin-android-extensions")
}

android {

  buildFeatures {
    viewBinding = true
  }
}

dependencies {
  implementation(project(":kite-core"))
  implementation(project(":kite-epoxy"))

  implementation(Kotlin.stdlib.jdk8)

  implementation(AndroidX.appCompat)
  implementation(AndroidX.constraintLayout)
  implementation(AndroidX.coreKtx)
  implementation(AndroidX.navigation.commonKtx)
  implementation(AndroidX.navigation.fragmentKtx)

  implementation(Google.android.material)

  testImplementation(Testing.junit4)

  androidTestImplementation(AndroidX.test.ext.junitKtx)
  androidTestImplementation(AndroidX.test.rules)
  androidTestImplementation(AndroidX.test.espresso.core)
}
