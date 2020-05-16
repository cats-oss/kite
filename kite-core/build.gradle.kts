plugins {
  `module-config`
  id("com.android.library")
  kotlin("android")
  id("kotlin-android-extensions")
  id("com.vanniktech.maven.publish")
}

android {
  testOptions {
    unitTests {
      isIncludeAndroidResources = true
    }
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}

dependencies {
  implementation(Kotlin.stdlib.jdk8)
  api(KotlinX.coroutines.core)

  implementation(AndroidX.lifecycle.viewModelKtx)
  implementation(AndroidX.lifecycle.liveDataKtx)
  implementation(AndroidX.lifecycle.runtimeKtx)
  implementation(AndroidX.lifecycle.commonJava8)

  testImplementation(Kotest.runnerJunit5)
  testImplementation(Kotest.assertionsCore)
  testImplementation(Kotest.property)
  testImplementation(Kotest.robolectric)
}
