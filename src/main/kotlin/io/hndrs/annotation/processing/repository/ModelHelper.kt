package io.hndrs.annotation.processing.repository

import org.springframework.core.annotation.MergedAnnotation
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Field
import javax.lang.model.element.Element
import javax.lang.model.element.PackageElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.TypeKind
import kotlin.reflect.KClass

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
    fun getIdTypeName(element: Element): String {
        return element.enclosedElements.firstOrNull {
            it.getAnnotation(Id::class.java) != null
        }?.let {
            getClass(it).simpleName.toString()
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
                    getClass(it),
                    fieldAnnoationName,
                    it.getAnnotation(Options::class.java)
                )
            }
    }

    private fun getClass(it: Element): KClass<*> {
        val type = it.asType()

        return when (type.kind) {
            TypeKind.DECLARED -> Class.forName(type.toString()).kotlin
            TypeKind.BOOLEAN -> Boolean::class
            TypeKind.BYTE -> Byte::class
            TypeKind.SHORT -> Short::class
            TypeKind.INT -> Int::class
            TypeKind.LONG -> Long::class
            TypeKind.CHAR -> Char::class
            TypeKind.FLOAT -> Float::class
            TypeKind.DOUBLE -> Double::class
            else -> throw Exception("Unknown type: $type, kind: ${type.kind}")
        }
    }

}
