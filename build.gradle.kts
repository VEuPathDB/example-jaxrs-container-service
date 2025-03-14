import org.veupathdb.lib.gradle.container.util.Logger.Level
import org.gradle.api.JavaVersion

plugins {
  java
  id("org.veupathdb.lib.gradle.container.container-utils") version "5.0.5"
  id("com.github.johnrengelman.shadow") version "7.1.2"
}

// configure VEupathDB container plugin
containerBuild {

  // Change if debugging the build process is necessary.
  logLevel = Level.Info

  // General project level configuration.
  project {

    // Project Name
    name = "example-service"

    // Project Group
    group = "org.veupathdb.service"

    // Project Version
    version = "1.0.0"

    // Project Root Package
    projectPackage = "org.veupathdb.service.demo"

    // Main Class Name
    mainClassName = "Main"
  }

  // Docker build configuration.
  docker {

    // Docker build context
    context = "."

    // Name of the target docker file
    dockerFile = "Dockerfile"

    // Resulting image tag
    imageName = "example-service"

  }
}

java {
  sourceCompatibility = JavaVersion.VERSION_21
  targetCompatibility = JavaVersion.VERSION_21
}

tasks.shadowJar {
  exclude("**/Log4j2Plugins.dat")
  archiveFileName.set("service.jar")
}

repositories {
  mavenCentral()
  mavenLocal()
  maven {
    name = "GitHubPackages"
    url  = uri("https://maven.pkg.github.com/veupathdb/maven-packages")
    credentials {
      username = if (extra.has("gpr.user")) extra["gpr.user"] as String? else System.getenv("GITHUB_USERNAME")
      password = if (extra.has("gpr.key")) extra["gpr.key"] as String? else System.getenv("GITHUB_TOKEN")
    }
  }
}

// ensures changing and dynamic modules are never cached
configurations.all {
  resolutionStrategy {
    cacheChangingModulesFor(0, TimeUnit.SECONDS)
    cacheDynamicVersionsFor(0, TimeUnit.SECONDS)
  }
}

dependencies {

  // Core lib
  implementation("org.veupathdb.lib:jaxrs-container-core:9.1.3")

  // Jersey
  implementation("org.glassfish.jersey.core:jersey-server:3.1.10")

  // Jackson
  implementation("com.fasterxml.jackson.core:jackson-databind:2.18.2")
  implementation("com.fasterxml.jackson.core:jackson-annotations:2.18.2")

  // Log4J
  implementation("org.apache.logging.log4j:log4j-api:2.24.3")
  implementation("org.apache.logging.log4j:log4j-core:2.24.3")

  // Metrics (can remove if not adding custom service metrics over those provided by container core)
  implementation("io.prometheus:simpleclient:0.16.0")
  implementation("io.prometheus:simpleclient_common:0.16.0")

  // Unit Testing
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.12.0")
  testImplementation("org.mockito:mockito-core:5.15.2")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.12.0")
}
