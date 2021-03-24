package io.hndrs.annotation.processing.repository

import java.time.Instant
import javax.annotation.processing.ProcessingEnvironment

interface RepsitoryGenerator {

    fun writeClass(
        processingEnv: ProcessingEnvironment,
        meta: MetaInformation,
    )

    companion object {

        fun generatedAnnotation(timeStamp: Instant): String {
            return "@Generated(value = [\"${SpringRepositoryProcessor::class.qualifiedName}\"], date = \"$timeStamp\", comments = \"\")"
        }
    }
}
