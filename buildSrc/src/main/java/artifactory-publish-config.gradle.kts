plugins {
  id("com.jfrog.artifactory")
}

artifactory {
  setContextUrl("http://oss.jfrog.org")
  withGroovyBuilder {
    "resolve" {
      "repository" {
        setProperty("repoKey", "jcenter")
      }
    }

    "publish" {
      "repository" {
        if (project.version.toString().endsWith("-SNAPSHOT")) {
          setProperty("repoKey", "oss-snapshot-local")
        } else {
          setProperty("repoKey", "oss-release-local")
        }

        setProperty("username", System.getProperty("bintrayUser"))
        setProperty("password", System.getProperty("bintrayKey"))
      }

      "defaults" {
        "publications"("maven")
      }
    }
  }
}
