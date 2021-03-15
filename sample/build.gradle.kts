plugins {
    id("org.springframework.boot")
    kotlin("jvm")
    kotlin("kapt")
    id("idea")
    id("io.spring.dependency-management")
}


val springBootDependencies: String by extra
val kotlinVersion: String by extra

java.sourceCompatibility = JavaVersion.VERSION_11
java.targetCompatibility = JavaVersion.VERSION_11

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

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    kapt(project(":"))
    compileOnly(project(":annotations"))
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}
