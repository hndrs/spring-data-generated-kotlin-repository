pluginManagement {
    val kotlinVersion: String by settings
    val springDependencyManagement: String by settings
    val springBootDependencies: String by settings

    plugins {
        id("org.springframework.boot").version(springBootDependencies)
        id("io.spring.dependency-management").version(springDependencyManagement)
        kotlin("jvm").version(kotlinVersion)
        kotlin("plugin.spring").version(kotlinVersion)
        kotlin("kapt").version(kotlinVersion)
        id("maven-publish")
        id("idea")
    }
    repositories {
    }
}

include("annotations")
project(":annotations").projectDir = File("annotations")

include("sample")
project(":sample").projectDir = File("sample")
