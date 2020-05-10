import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.TestedExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

project.afterEvaluate {
  plugins.all {
    when (this) {
      is LibraryPlugin -> {
        extensions.getByType<TestedExtension>().androidCommonConfig(project.gradle.startParameter)
      }
      is AppPlugin -> {
        extensions.getByType<BaseAppModuleExtension>().androidAppConfig()
        extensions.getByType<TestedExtension>().androidCommonConfig(project.gradle.startParameter)
      }
    }
  }
  commonConfig()
}

fun TestedExtension.androidCommonConfig(startParameter: StartParameter) {
  setCompileSdkVersion(AndroidSdk.compileSdk)

  defaultConfig {
    // set minSdkVersion to 21 for android tests to avoid multi-dexing.
    val testTaskKeywords = listOf("androidTest", "connectedCheck")
    val isTestBuild = startParameter.taskNames.any { taskName ->
      testTaskKeywords.any { keyword ->
        taskName.contains(keyword, ignoreCase = true)
      }
    }
    if (!isTestBuild) {
      minSdkVersion(AndroidSdk.minSdk)
    } else {
      minSdkVersion(AndroidSdk.testMinSdk)
    }
    targetSdkVersion(AndroidSdk.targetSdk)

    versionCode = Coordinates.VERSION_CODE
    versionName = Coordinates.VERSION_NAME

    compileOptions {
      sourceCompatibility = JavaVersion.VERSION_1_8
      targetCompatibility = JavaVersion.VERSION_1_8
    }

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    consumerProguardFiles("consumer-rules.pro")
  }

  buildTypes {
    getByName("release") {
      isMinifyEnabled = false
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
      )
    }
  }

  lintOptions {
    isWarningsAsErrors = true
    isAbortOnError = true
  }

  testOptions.animationsDisabled = true
}

fun BaseAppModuleExtension.androidAppConfig() {
  defaultConfig {
    applicationId = AppCoordinates.APP_ID
  }
}

fun Project.commonConfig() {

  tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = JavaVersion.VERSION_1_8.toString()
    targetCompatibility = JavaVersion.VERSION_1_8.toString()
  }

  tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "1.8"
  }

  tasks.withType<Test>().configureEach {
    maxParallelForks = Runtime.getRuntime().availableProcessors() * 2
    testLogging {
      events(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
    }
  }
}
