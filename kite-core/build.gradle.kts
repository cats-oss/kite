plugins {
  `module-config`
  id("com.android.library")
  kotlin("android")
  id("kotlin-android-extensions")
  id("com.vanniktech.maven.publish")
}

dependencies {
  implementation(Kotlin.stdlib.jdk8)
  api(KotlinX.coroutines.core)

  implementation(AndroidX.lifecycle.viewModelKtx)
  implementation(AndroidX.lifecycle.liveDataKtx)
  implementation(AndroidX.lifecycle.runtimeKtx)
  implementation(AndroidX.lifecycle.commonJava8)
}
