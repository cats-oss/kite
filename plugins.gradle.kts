import java.util.Properties

gradle.settingsEvaluated {
  pluginManagement {
    val resolutionStrategyConfig: String? by extra
    if (resolutionStrategyConfig == "false" || file("versions.properties").canRead()
        .not()
    ) return@pluginManagement
    @Suppress("UNCHECKED_CAST")
    val properties: Map<String, String> = Properties().apply {
      load(file("versions.properties").reader())
    } as Map<String, String>
    resolutionStrategy.eachPlugin {
      val pluginId = requested.id.id
      val version = properties["plugin.$pluginId"]
      val message = if (version != null) {
        when (pluginId) {
          "binary-compatibility-validator" -> {
            useModule("org.jetbrains.kotlinx:binary-compatibility-validator:$version")
          }
          else -> useVersion(version)
        }
        "ResolutionStrategy used version=$version for plugin=$pluginId"
      } else {
        "ResolutionStrategy did not find a version for $pluginId"
      }
      if (resolutionStrategyConfig == "verbose") println(message)
    }
  }
}
