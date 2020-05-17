plugins {
  `module-config`
  id("com.android.library")
  kotlin("android")
  id("kotlin-android-extensions")
  id("com.vanniktech.maven.publish")
}

dependencies {
  api(project(":kite-core"))

  implementation(Kotlin.stdlib.jdk8)

  api(AndroidX.fragmentKtx)
  api(AndroidX.activityKtx)
  api(AndroidX.lifecycle.viewModelKtx)
  api(AndroidX.lifecycle.liveDataKtx)
  api(AndroidX.lifecycle.runtimeKtx)
  api(AndroidX.lifecycle.commonJava8)

  testImplementation(Testing.junit4)
}
