import java.util.Properties

Properties().apply {
  load(File("${rootDir.parentFile}/versions.properties").inputStream())
  forEach { k, v -> extra.set(k as String, v) }
}
