plugins {
  `module-config`
  id("com.android.application")
  kotlin("android")
  id("kotlin-android-extensions")
}

android {

  @Suppress("UnstableApiUsage")
  buildFeatures {
    viewBinding = true
  }

  lintOptions {
    disable(
      "HardcodedText",
      "VectorRaster",
      "ContentDescription",
      "SetTextI18n"
    )
  }
}

dependencies {
  implementation(project(":kite-core"))
  implementation(project(":kite-runtime"))

  implementation(Kotlin.stdlib.jdk8)

  implementation(AndroidX.appCompat)
  implementation(AndroidX.constraintLayout)
  implementation(AndroidX.coreKtx)
  implementation(AndroidX.navigation.commonKtx)
  implementation(AndroidX.navigation.fragmentKtx)
  implementation(AndroidX.swipeRefreshLayout)

  implementation(Google.android.material)

  implementation(Airbnb.epoxy)

  // testImplementation will throw exception with launchFragmentInContainer
  implementation(AndroidX.fragmentTesting)
  testImplementation(project(":kite-testing"))
  testImplementation(Testing.junit4)
  testImplementation(AndroidX.test.ext.junitKtx)
  testImplementation(AndroidX.test.ext.truth)
  testImplementation(AndroidX.test.rules)
  testImplementation(AndroidX.test.espresso.core)
  testImplementation(AndroidX.test.espresso.contrib)
  testImplementation(AndroidX.test.coreKtx)
  testImplementation(AndroidX.archCore.testing)
  testImplementation(Testing.MockK.mockK)
  testImplementation(Testing.roboElectric)

  androidTestImplementation(AndroidX.test.ext.junitKtx)
  androidTestImplementation(AndroidX.test.rules)
  androidTestImplementation(AndroidX.test.espresso.core)
}
