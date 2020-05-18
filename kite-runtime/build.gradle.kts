plugins {
  `module-config`
  id("com.android.library")
  kotlin("android")
  id("com.vanniktech.maven.publish")
  `bintray-publish-config`
}

dependencies {
  api(project(":kite-core"))

  implementation(Kotlin.stdlib.jdk8)

  api(AndroidX.appCompat)
  api(AndroidX.fragmentKtx)
  api(AndroidX.activityKtx)
  api(AndroidX.lifecycle.viewModelKtx)
  api(AndroidX.lifecycle.liveDataKtx)
  api(AndroidX.lifecycle.runtimeKtx)
  api(AndroidX.lifecycle.commonJava8)

  testImplementation(project(":kite-androidtestcommon"))
  testImplementation(AndroidX.test.coreKtx)
  testImplementation(AndroidX.fragmentTesting)
}
