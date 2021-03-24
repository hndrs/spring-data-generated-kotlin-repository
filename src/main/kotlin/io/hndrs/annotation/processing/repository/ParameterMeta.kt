package io.hndrs.annotation.processing.repository

data class ParameterMeta(
    val propertyName: String,
    val propertyTypeQualifiedName: String,
    val propertyTypeSimpleName: String,
    val fieldAnnoationName: String?,
    val options: Options?
) {
    fun queryKey(): String {
        return this.fieldAnnoationName ?: this.propertyName
    }
}
