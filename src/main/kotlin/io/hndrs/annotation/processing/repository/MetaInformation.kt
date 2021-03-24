package io.hndrs.annotation.processing.repository

import org.springframework.data.annotation.Id
import javax.lang.model.element.Element

object MetaResolver {
}

/**
 *
 */
data class MetaInformation(
    val packageName: String,
    val entityName: String,
    val generateRepository: GenerateRepository,
    val idMeta: IdMeta,
    val variables: List<VariableMeta>
) {

    data class IdMeta(
        val propertyTypeQualifiedName: String,
        val propertyTypeSimpleName: String,
    )

    data class VariableMeta(
        val propertyName: String,
        val propertyTypeQualifiedName: String,
        val propertyTypeSimpleName: String,
        val propertyGenericTypeArgument:String?,
        val options: Options?
    )

    companion object {
        fun create(element: Element): MetaInformation {
            return MetaInformation(
                element.packageName(),
                element.simpleTypeName(),
                element.getAnnotation(GenerateRepository::class.java),
                getIdMeta(element),
                getVariableMetas(element)
            )
        }


        private fun getIdMeta(element: Element): IdMeta {
            return element.enclosedElements.firstOrNull {
                it.getAnnotation(Id::class.java) != null
            }?.let {
                IdMeta(
                    it.qualifiedTypeName(),
                    it.simpleTypeName()
                )
            } ?: throw RepositoryGeneratorException("Could not find @Id annotated property in ${element.simpleName}")
        }

        private fun getVariableMetas(element: Element): List<VariableMeta> {
            return element.variableElements()
                .map {
                    VariableMeta(
                        it.mappedPropertyName(),
                        it.qualifiedTypeName(),
                        it.simpleTypeName(),
                        it.genericTypeArgumentName(),
                        it.getAnnotation(Options::class.java)
                    )
                }
        }
    }
}
