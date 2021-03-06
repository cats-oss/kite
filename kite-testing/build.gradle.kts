plugins {
  `module-config`
  id("com.android.library")
  kotlin("android")
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
  api(project(":kite-core"))
  api(project(":kite-runtime"))

  api(Kotlin.stdlib.jdk8)
  api(KotlinX.coroutines.test)

  api(AndroidX.appCompat)
  api(AndroidX.test.coreKtx)

  testImplementation(project(":androidtestcommon"))
  testImplementation(AndroidX.fragmentTesting)
}
