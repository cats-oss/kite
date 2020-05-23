plugins {
  `module-config`
  id("com.android.library")
  kotlin("android")
  id("kotlin-android-extensions")
  id("com.vanniktech.maven.publish")
  `artifactory-publish-config`
  `bintray-publish-config`
}

dependencies {
  api(project(":kite-core"))
  api(project(":kite-runtime"))

  api(Kotlin.stdlib.jdk8)
  api(KotlinX.coroutines.test)

  api(AndroidX.appCompat)
  api(AndroidX.test.coreKtx)

  testImplementation(project(":androidtestcommon"))
  testImplementation(AndroidX.fragmentTesting)
}
