import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.TestedExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension

project.afterEvaluate {
  plugins.all {
    when (this) {
      is LibraryPlugin -> {
        extensions.getByType<LibraryExtension>().androidLibraryConfig()
        extensions.getByType<TestedExtension>().androidCommonConfig(project.gradle.startParameter)
        libraryConfig()
      }
      is AppPlugin -> {
        extensions.getByType<BaseAppModuleExtension>().androidAppConfig()
        extensions.getByType<TestedExtension>().androidCommonConfig(project.gradle.startParameter)
      }
      is JavaLibraryPlugin -> {
        libraryConfig()
      }
    }
  }
  commonConfig()
}
