import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
  id("binary-compatibility-validator")
  id("io.gitlab.arturbosch.detekt")
  id("org.jlleitschuh.gradle.ktlint")
  id("com.github.ben-manes.versions")
  id("com.vanniktech.maven.publish") apply false
}

allprojects {
  repositories {
    google()
    mavenCentral()
    jcenter()
  }
}

subprojects {
  apply {
    plugin("org.jlleitschuh.gradle.ktlint")
  }

  ktlint {
    debug.set(false)
    version.set("0.37.0")
    verbose.set(true)
    android.set(false)
    outputToConsole.set(true)
    ignoreFailures.set(false)
    enableExperimentalRules.set(true)
    filter {
      exclude("**/generated/**")
      include("**/kotlin/**")
    }
  }

  if (name != "kite-sample") {
    apply {
      plugin("io.gitlab.arturbosch.detekt")
    }

    detekt {
      config = rootProject.files("config/detekt/detekt.yml")
      reports {
        html {
          enabled = true
          destination = file("build/reports/detekt.html")
        }
      }
    }
  }
}

apiValidation {
  ignoredProjects = mutableSetOf("kite-sample", "testcommon", "androidtestcommon")
}

tasks.register("clean", Delete::class.java) {
  delete(rootProject.buildDir)
}

tasks.withType<DependencyUpdatesTask> {
  rejectVersionIf {
    isNonStable(candidate.version)
  }
}

fun isNonStable(version: String) = "^[0-9,.v-]+(-r)?$".toRegex().matches(version).not()
