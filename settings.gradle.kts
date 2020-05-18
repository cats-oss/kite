import de.fayard.dependencies.bootstrapRefreshVersionsAndDependencies

pluginManagement {
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
    jcenter()
  }
}

apply(from = "plugins.gradle.kts")

buildscript {
  repositories { gradlePluginPortal() }
  dependencies.classpath("de.fayard:dependencies:0.5.8")
}

bootstrapRefreshVersionsAndDependencies()

rootProject.name = ("kite")

include(
  "kite-sample",
  "kite-testcommon",
  "kite-androidtestcommon",
  "kite-core",
  "kite-runtime",
  "kite-epoxy",
  "kite-testing"
)
