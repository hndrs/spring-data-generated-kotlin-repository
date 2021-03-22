package io.hndrs.annotation.processing.repository

import kotlin.reflect.KClass

data class ParameterMeta(
    val propertyName: String,
    val propertyType: KClass<*>,
    val fieldAnnoationName: String?,
    val options: Options?
) {
    fun queryKey(): String {
        return this.fieldAnnoationName ?: this.propertyName
    }
}
