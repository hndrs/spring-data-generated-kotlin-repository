package io.hndrs.annotation.processing.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
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

}
