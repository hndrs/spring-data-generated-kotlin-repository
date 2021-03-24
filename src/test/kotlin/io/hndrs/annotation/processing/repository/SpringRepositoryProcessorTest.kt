package io.hndrs.annotation.processing.repository

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.KotlinCompilation.ExitCode
import com.tschuchort.compiletesting.SourceFile
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.Aware
import org.springframework.context.ApplicationContextAware
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.repository.MongoRepository
import java.io.File
import javax.lang.model.SourceVersion

internal class SpringRepositoryProcessorTest {


    @Test
    @DisplayName("SupportedSource Version is latest")
    fun getSupportedSourceVersion() {
        assertEquals(
            SourceVersion.latest(), SpringRepositoryProcessor().supportedSourceVersion
        )
    }

    @Test
    @DisplayName("Supported Annotation")
    fun getSupportedAnnotationTypes() {
        assertEquals(
            setOf("io.hndrs.annotation.processing.repository.GenerateRepository"), SpringRepositoryProcessor().supportedAnnotationTypes
        )
    }

    @Test
    @DisplayName("Simple Entity")
    fun compileSimpleEntity() {
        val entity = SourceFile.fromPath(File("src/test/kotlin/io/hndrs/test/SimpleEntity.kt"))
        val result = KotlinCompilation().apply {
            sources = listOf(entity)
            classpaths = additionalClassPaths()
            annotationProcessors = listOf(SpringRepositoryProcessor())
            //kaptKotlinGeneratedDir = File("build/generated/source/kaptKotlin/main")
            kaptArgs[KotlinCompilation.OPTION_KAPT_KOTLIN_GENERATED]
        }.compile()

        assertEquals(ExitCode.OK, result.exitCode)
    }

    @Test
    @DisplayName("ExtensionOnly Entity")
    fun compileExtensionOnlyEntity() {
        val entity = SourceFile.fromPath(File("src/test/kotlin/io/hndrs/test/ExtensionOnlyEntity.kt"))
        val result = KotlinCompilation().apply {
            sources = listOf(entity)
            classpaths = additionalClassPaths()
            annotationProcessors = listOf(SpringRepositoryProcessor())
            //kaptKotlinGeneratedDir = File("build/generated/source/kaptKotlin/main")
            kaptArgs[KotlinCompilation.OPTION_KAPT_KOTLIN_GENERATED]
        }.compile()

        assertEquals(ExitCode.OK, result.exitCode)
    }

    @Disabled("Currently there is no support for nested classes. But we keep this test to implement it")
    @Test
    @DisplayName("Nested SimpleEntity")
    fun compileTestNestedSimple() {
        val entity = SourceFile.fromPath(File("src/test/kotlin/io/hndrs/test/NestedSimpleEntity.kt"))
        val result = KotlinCompilation().apply {
            sources = listOf(entity)
            classpaths = additionalClassPaths()
            annotationProcessors = listOf(SpringRepositoryProcessor())
            //kaptKotlinGeneratedDir = File("build/generated/source/kaptKotlin/main")
            kaptArgs[KotlinCompilation.OPTION_KAPT_KOTLIN_GENERATED]
        }.compile()

        assertEquals(ExitCode.OK, result.exitCode)
    }

    @Test
    @DisplayName("All Options")
    fun compileAllOptions() {
        val entity = SourceFile.fromPath(File("src/test/kotlin/io/hndrs/test/AllOptionsEntity.kt"))
        val result = KotlinCompilation().apply {
            sources = listOf(entity)
            classpaths = additionalClassPaths()
            annotationProcessors = listOf(SpringRepositoryProcessor())
            kaptArgs[KotlinCompilation.OPTION_KAPT_KOTLIN_GENERATED]
        }.compile()

        assertEquals(ExitCode.COMPILATION_ERROR, result.exitCode)
    }

    @Test
    @DisplayName("Missing @Id Annotation")
    fun compileMissingIdAnnotation() {
        val entity = SourceFile.fromPath(File("src/test/kotlin/io/hndrs/test/SimpleEntityMissingIdAnnotation.kt"))
        val result = KotlinCompilation().apply {
            sources = listOf(entity)
            classpaths = additionalClassPaths()
            annotationProcessors = listOf(SpringRepositoryProcessor())
            kaptArgs[KotlinCompilation.OPTION_KAPT_KOTLIN_GENERATED]
        }.compile()

        assertEquals(ExitCode.COMPILATION_ERROR, result.exitCode)
    }

    @Test
    @DisplayName("Missing PackageName")
    fun compileMissingPackageName() {
        val entity = SourceFile.fromPath(File("src/test/kotlin/io/hndrs/test/SimpleEntityMissingPackageName.kt"))
        val result = KotlinCompilation().apply {
            sources = listOf(entity)
            classpaths = additionalClassPaths()
            annotationProcessors = listOf(SpringRepositoryProcessor())
            kaptArgs[KotlinCompilation.OPTION_KAPT_KOTLIN_GENERATED]
        }.compile()

        assertEquals(ExitCode.COMPILATION_ERROR, result.exitCode)
    }

    private fun additionalClassPaths(): List<File> {
        return listOf(
            File(this::class.java.getProtectionDomain().getCodeSource().getLocation().getPath()),
            File(GenerateRepository::class.java.getProtectionDomain().getCodeSource().getLocation().getPath()),
            File(Id::class.java.getProtectionDomain().getCodeSource().getLocation().getPath()),
            File(MongoRepository::class.java.getProtectionDomain().getCodeSource().getLocation().getPath()),
            File(ApplicationContextAware::class.java.getProtectionDomain().getCodeSource().getLocation().getPath()),
            File(Aware::class.java.getProtectionDomain().getCodeSource().getLocation().getPath()),
        )
    }

}
