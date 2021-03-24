package io.hndrs.annotation.processing.repository

import org.springframework.core.annotation.MergedAnnotation
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Field
import javax.lang.model.element.Element
import javax.lang.model.element.PackageElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeKind

object ModelHelper {

    fun getPackageName(element: Element): String {
        var foundPackage: Boolean = false
        var nextElement: Element = element
        var packageName: String? = null
        do {
            val enclosingElement = nextElement.enclosingElement
            if (enclosingElement is PackageElement) {
                packageName = enclosingElement.qualifiedName.toString()
                foundPackage = true
            } else {
                nextElement = enclosingElement
            }
        } while (!foundPackage)
        if (packageName != null && !packageName.isBlank()) {
            return packageName
        }
        throw RepositoryGeneratorException("Could not resolve package name for $element")
    }

    @Throws(RepositoryGeneratorException::class)
    fun getIdTypeSimpleName(element: Element): String {
        return element.enclosedElements.firstOrNull {
            it.getAnnotation(Id::class.java) != null
        }?.let {
            getSimpleName(it)
        } ?: throw RepositoryGeneratorException("Could not find @Id annotated property in ${element.simpleName}")
    }

    fun getParameterMetas(element: Element): List<ParameterMeta> {
        return element.enclosedElements
            .filterIsInstance<VariableElement>()
            .map {
                val fieldAnnoationName = it.getAnnotation(Field::class.java)
                    ?.let { MergedAnnotation.from(it) }
                    ?.getString("name")
                ParameterMeta(
                    it.simpleName.toString(),
                    getQualifiedName(it),
                    getSimpleName(it),
                    getGenericSimpleName(it),
                    fieldAnnoationName,
                    it.getAnnotation(Options::class.java)
                )
            }
    }

    private fun getQualifiedName(it: Element): String {
        val type = it.asType()

        return when (type.kind) {
            TypeKind.DECLARED -> (type as DeclaredType).asElement().toString()
            else -> throw Exception("Unknown type: $type, kind: ${type.kind}")
        }
    }

    private fun getGenericSimpleName(element: Element): String? {
        val declaredType = element.asType() as DeclaredType
        if (declaredType.typeArguments.size == 1) {
            return (declaredType.typeArguments[0] as DeclaredType).asElement().simpleName.toString()
        }
        return null
    }

    private fun getSimpleName(it: Element): String {
        val type = it.asType()

        return when (type.kind) {
            TypeKind.DECLARED -> declaredTypeSimple(type as DeclaredType)
            else -> throw Exception("Unknown type: $type, kind: ${type.kind}")
        }
    }

    private fun declaredTypeSimple(declaredType: DeclaredType): String {
        val simpleName = declaredType.asElement().simpleName.toString()
        return if (declaredType.typeArguments.isEmpty()) {
            simpleName
        } else if (declaredType.typeArguments.size == 1) {
            val genericSimpleType = ((declaredType.typeArguments[0]) as DeclaredType).asElement().simpleName.toString()

            "$simpleName<$genericSimpleType>"
        } else {
            throw RepositoryGeneratorException("Can not resolve generics with more than 1 generic argument")
        }
    }
}
