plugins {
  `kotlin-dsl`
}

apply(from = "versions.gradle.kts")

repositories {
  jcenter()
  google()
}

val kotlinVersion = extra["version.kotlin"]

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
  implementation("com.android.tools.build:gradle:4.2.0-alpha01")
  implementation("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.5")
  implementation("org.jfrog.buildinfo:build-info-extractor-gradle:4.15.2")
}
