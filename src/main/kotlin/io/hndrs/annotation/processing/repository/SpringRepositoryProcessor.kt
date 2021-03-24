package io.hndrs.annotation.processing.repository

import com.google.auto.service.AutoService
import io.hndrs.annotation.processing.repository.GenerateRepository.Type
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic


@SupportedAnnotationTypes(
    "io.hndrs.annotation.processing.repository.GenerateRepository"
)
@AutoService(Processor::class)
class SpringRepositoryProcessor : AbstractProcessor() {

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {

        for (annotation in annotations!!) {
            val annotatedElements: Set<Element> = roundEnv.getElementsAnnotatedWith(annotation)
            annotatedElements.forEach {
                try {

                    val metaInformation = MetaInformation.create(it)
                    val generateRepository = it.getAnnotation(GenerateRepository::class.java)
                    when (generateRepository.type) {
                        Type.MONGO -> MongoRepsitoryGenerator.writeClass(
                            processingEnv,
                            metaInformation
                        )
                    }

                } catch (e: Exception) {
                    processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, e.message)
                }
            }
        }
        return true
    }

}

