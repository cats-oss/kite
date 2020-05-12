plugins {
  `module-config`
  id("com.android.library")
  kotlin("android")
  id("kotlin-android-extensions")
  id("com.vanniktech.maven.publish")
}

dependencies {
  implementation(Kotlin.stdlib.jdk8)

  implementation(AndroidX.appCompat)
  implementation(AndroidX.coreKtx)
  implementation(AndroidX.fragmentKtx)
  implementation(AndroidX.activityKtx)

  testImplementation(Testing.junit4)
}
