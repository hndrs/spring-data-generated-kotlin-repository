package io.hndrs.annotation.processing.repository

import java.time.Instant
import javax.annotation.processing.ProcessingEnvironment

interface RepsitoryGenerator {

    fun writeClass(
        processingEnv: ProcessingEnvironment,
        packageName: String,
        entityTypeName: String,
        idTypeName: String,
        params: List<ParameterMeta>,
        extensionOnly: Boolean
    )

    companion object {

        fun generatedAnnotation(timeStamp: Instant): String {
            return "@Generated(value = [\"${SpringRepositoryProcessor::class.qualifiedName}\"], date = \"$timeStamp\", comments = \"\")"
        }
    }
}
