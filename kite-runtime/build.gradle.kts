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

  testImplementation(Testing.junit4)
}
