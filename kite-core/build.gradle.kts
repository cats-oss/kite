plugins {
  `module-config`
  id("com.android.library")
  kotlin("android")
  id("kotlin-android-extensions")
  id("com.vanniktech.maven.publish")
}

dependencies {
  implementation(project(":kite-common"))
  implementation(Kotlin.stdlib.jdk8)
  api(KotlinX.coroutines.core)
}
