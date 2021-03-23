import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


buildscript {
    repositories {
        mavenCentral()
        maven(url = "https://repo.spring.io/plugins-release")
    }
    dependencies {
        classpath("io.spring.gradle:propdeps-plugin:0.0.9.RELEASE")
    }
}
apply(plugin = "propdeps")
apply(plugin = "propdeps-idea")

val springBootDependencies: String by extra
val kotlinVersion: String by extra

plugins {
    id("org.sonarqube").version("3.1.1")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("kapt")
    id("maven-publish")
    id("idea")
    id("signing")
    jacoco
    id("io.hndrs.publishing-info").version("2.0.0")
}

val isRelease = project.hasProperty("release")

group = "io.hndrs"
version = "1.0.0".plus(if (isRelease) "" else "-SNAPSHOT")
java.sourceCompatibility = JavaVersion.VERSION_11
java.targetCompatibility = JavaVersion.VERSION_11


repositories {
    mavenCentral()
}

sonarqube {
    properties {
        property("sonar.projectKey", "hndrs_spring-data-generated-kotlin-repository")
        property("sonar.organization", "hndrs")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.exclusions", "**/sample/**")
    }
}

java {
    withJavadocJar()
}

configure<JacocoPluginExtension> {
    toolVersion = "0.8.6"
}

tasks.withType<JacocoReport> {
    reports {
        xml.apply {
            isEnabled = true
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    kapt(group = "com.google.auto.service", name = "auto-service", version = "1.0-rc7")
    implementation(group = "com.google.auto.service", name = "auto-service", version = "1.0-rc7")

    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation(project(":annotations"))

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    testImplementation(platform("org.junit:junit-bom:5.7.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.mockk:mockk:1.10.6")
}

dependencyManagement {
    resolutionStrategy {
        cacheChangingModulesFor(0, "seconds")
    }
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:$springBootDependencies") {
            bomProperty("kotlin.version", kotlinVersion)
        }
    }
}

val sourcesJarSubProject by tasks.creating(Jar::class) {
    dependsOn("classes")
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

publishing {
    repositories {
        if (isRelease) {
            maven {
                name = "release"
                url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
                credentials {
                    username = System.getenv("SONATYPE_USER")
                    password = System.getenv("SONATYPE_PASSWORD")
                }
            }
        } else {
            maven {
                name = "snapshot"
                url = uri("https://maven.pkg.github.com/hndrs/spring-data-mongodb-kotlin-extensior")
                credentials {
                    username = System.getenv("GITHUB_ACTOR")
                    password = System.getenv("GITHUB_TOKEN")
                }
            }
        }
    }
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
            artifact(sourcesJarSubProject)

            groupId = rootProject.group as? String
            artifactId = rootProject.name
            version = "${rootProject.version}${project.findProperty("version.appendix") ?: ""}"
        }
    }
    val signingKey: String? = System.getenv("SIGNING_KEY")
    val signingPassword: String? = System.getenv("SIGNING_PASSWORD")
    if (signingKey != null && signingPassword != null) {
        signing {
            useInMemoryPgpKeys(groovy.json.StringEscapeUtils.unescapeJava(signingKey), signingPassword)
            sign(publications[project.name])
        }
    }
}

publishingInfo {
    name = rootProject.name
    description = "Kotlin Extensions for Spring Data MongoDB"
    url = "https://github.com/hndrs/spring-data-mongodb-kotlin-extension"
    license = io.hndrs.gradle.plugin.License(
        "https://github.com/hndrs/spring-data-mongodb-kotlin-extension/blob/main/LICENSE",
        "MIT License"
    )
    developers = listOf(
        io.hndrs.gradle.plugin.Developer("marvinschramm", "Marvin Schramm", "marvin.schramm@gmail.com")
    )
    organization = io.hndrs.gradle.plugin.Organization("hndrs", "https://oss.hndrs.io")
    scm = io.hndrs.gradle.plugin.Scm(
        "scm:git:git://github.com/hndrs/spring-data-mongodb-kotlin-extension",
        "https://github.com/hndrs/spring-data-mongodb-kotlin-extension"
    )
}
