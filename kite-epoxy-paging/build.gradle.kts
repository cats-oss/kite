plugins {
  `module-config`
  id("com.android.library")
  kotlin("android")
  id("com.vanniktech.maven.publish")
  `artifactory-publish-config`
  `bintray-publish-config`
}

dependencies {
  api(project(":kite-epoxy"))

  api(Kotlin.stdlib.jdk8)

  api(Airbnb.epoxy)
  api(Airbnb.epoxyPaging)

  testImplementation(project(":androidtestcommon"))
}
