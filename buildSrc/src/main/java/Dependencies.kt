import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

fun Project.commonTestImplementation() {
  dependencies {
    "testImplementation"(Kotest.runnerJunit5)
    "testImplementation"(Kotest.assertionsCore)
    "testImplementation"(Kotest.property)
    "testImplementation"(Kotest.robolectric)
  }
}
