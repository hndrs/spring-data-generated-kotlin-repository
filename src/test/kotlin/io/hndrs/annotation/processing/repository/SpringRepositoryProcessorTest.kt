package io.hndrs.annotation.processing.repository

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.KotlinCompilation.ExitCode
import com.tschuchort.compiletesting.SourceFile
import org.junit.jupiter.api.Assertions.assertEquals
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
    @DisplayName("Generate Respository ExitCode.OK")
    fun compileTestSuccessful() {
        val simpleEntity = SourceFile.fromPath(File("src/test/kotlin/io/hndrs/test/SimpleEntity.kt"))
        val result = KotlinCompilation().apply {
            sources = listOf(simpleEntity)
            classpaths = additionalClassPaths()
            annotationProcessors = listOf(SpringRepositoryProcessor())
            //kaptKotlinGeneratedDir = File("build/generated/source/kaptKotlin/main")
            kaptArgs[KotlinCompilation.OPTION_KAPT_KOTLIN_GENERATED]
        }.compile()

        assertEquals(ExitCode.OK, result.exitCode)
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
