plugins {
  `module-config`
  id("java-library")
  kotlin("jvm")
  id("com.vanniktech.maven.publish")
  `bintray-publish-config`
}

dependencies {
  implementation(Kotlin.stdlib.jdk8)
  api(KotlinX.coroutines.core)

  testImplementation(project(":kite-testcommon"))
}
