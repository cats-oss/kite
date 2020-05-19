import com.jfrog.bintray.gradle.BintrayExtension
import java.util.Date

plugins {
  id("com.jfrog.bintray")
}

bintray {
  user = System.getProperty("bintrayUser")
  key = System.getProperty("bintrayKey")
  setPublications("maven")

  pkg(closureOf<BintrayExtension.PackageConfig> {
    repo = "maven"
    userOrg = "cats-oss"
    name = extra["POM_ARTIFACT_ID"].toString()
    vcsUrl = extra["POM_SCM_URL"].toString()
    websiteUrl = extra["POM_URL"].toString()
    setLicenses("Apache-2.0")
    version(closureOf<BintrayExtension.VersionConfig> {
      name = project.version.toString()
      released = Date().toString()
    })
  })
}
