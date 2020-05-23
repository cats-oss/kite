plugins {
  `module-config`
  id("java-library")
  kotlin("jvm")
  id("com.vanniktech.maven.publish")
  `artifactory-publish-config`
  `bintray-publish-config`
}

dependencies {
  api(Kotlin.stdlib.jdk8)
  api(KotlinX.coroutines.core)

  testImplementation(project(":testcommon"))
}
