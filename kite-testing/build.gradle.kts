plugins {
  `module-config`
  id("com.android.library")
  kotlin("android")
  id("kotlin-android-extensions")
  id("com.vanniktech.maven.publish")
  `bintray-publish-config`
}

dependencies {
  implementation(project(":kite-core"))
  implementation(project(":kite-runtime"))
  implementation(Kotlin.stdlib.jdk8)
  api(KotlinX.coroutines.test)

  implementation(AndroidX.appCompat)
  implementation(AndroidX.test.coreKtx)

  testImplementation(project(":kite-androidtestcommon"))
  testImplementation(AndroidX.fragmentTesting)
}
