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

  api(Kotlin.stdlib.jdk8)

  api(AndroidX.appCompat)
  api(AndroidX.fragmentKtx)
  api(AndroidX.activityKtx)
  api(AndroidX.lifecycle.viewModelKtx)
  api(AndroidX.lifecycle.liveDataKtx)
  api(AndroidX.lifecycle.runtimeKtx)
  api(AndroidX.lifecycle.commonJava8)
  api(AndroidX.lifecycle.viewModelSavedState)

  testImplementation(project(":androidtestcommon"))
  testImplementation(AndroidX.test.coreKtx)
  testImplementation(AndroidX.fragmentTesting)
}
