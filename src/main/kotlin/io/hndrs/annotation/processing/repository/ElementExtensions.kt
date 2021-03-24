package io.hndrs.annotation.processing.repository

import org.springframework.core.annotation.MergedAnnotation
import org.springframework.data.mongodb.core.mapping.Field
import javax.lang.model.element.Element
import javax.lang.model.element.PackageElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeKind

fun Element.mappedPropertyName(): String {
    val annotatedName = this.getAnnotation(Field::class.java)
        ?.let { MergedAnnotation.from(it) }
        ?.getString("name")
    val variableName = this.simpleName.toString()

    return annotatedName ?: variableName
}

fun Element.qualifiedTypeName(): String {
    val type = this.asType()

    return when (type.kind) {
        TypeKind.DECLARED -> (type as DeclaredType).asElement().toString()
        else -> throw Exception("Unknown type: $type, kind: ${type.kind}")
    }
}

fun Element.simpleTypeName(): String {
    val type = this.asType() as DeclaredType
    val simpleName = type.asElement().simpleName.toString()
    val genericTypeString = type.typeArguments
        .map { it as DeclaredType }
        .map { it.asElement() }
        .map { it.simpleName.toString() }
        .joinToString(separator = ", ")
        .takeIf { it.isNotBlank() }

    return if (type.typeArguments.isEmpty()) {
        simpleName
    } else {
        "$simpleName<$genericTypeString>"
    }
}

fun Element.genericTypeArgumentName(): String? {
    val declaredType = this.asType() as DeclaredType
    return declaredType.typeArguments
        .map { it as DeclaredType }
        .map { it.asElement() }
        .map { it.simpleName.toString() }
        .joinToString(separator = ", ")
        .takeIf { it.isNotBlank() }
}

fun Element.variableElements(): List<VariableElement> {
    return this.enclosedElements
        .filterIsInstance<VariableElement>()
}

fun Element.packageName(): String {
    var foundPackage: Boolean = false
    var nextElement: Element = this
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
    throw RepositoryGeneratorException("Could not resolve package name for ${this.simpleName}")
}
