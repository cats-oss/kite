plugins {
  `module-config`
  id("java-library")
  kotlin("jvm")
  id("com.vanniktech.maven.publish")
  `artifactory-publish-config`
  `bintray-publish-config`
}

afterEvaluate {
  tasks.dokka {
    outputDirectory = "$rootDir/docs/api"
    outputFormat = "gfm"
  }
}

dependencies {
  api(Kotlin.stdlib.jdk8)
  api(KotlinX.coroutines.core)

  testImplementation(project(":testcommon"))
}
