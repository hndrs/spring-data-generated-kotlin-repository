package io.hndrs.annotation

import java.lang.annotation.Inherited

@Target(AnnotationTarget.FIELD, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.SOURCE)
@Inherited
annotation class Options(
    /**
     * Option to update [Instant] with [java.time.Instant.now] which
     * might be useful for fields like lastModifiedAt.
     * Note:
     * This option has no effect on any other type than [Instant]
     */
    val updateOnSave: Boolean = false,

    /**
     * Option to exclude field as query parameter
     */
    val exclude: Boolean = false,

    /**
     * Option to add 'less than equals field' as query parameter
     */
    val withLte: Boolean = false,

    /**
     * Option to add 'less than field' as query parameter
     */
    val withLt: Boolean = false,

    /**
     * Option to add 'greater than equals field' as query parameter
     */
    val withGte: Boolean = false,

    /**
     * Option to add 'greater than field' as query parameter
     */
    val withGt: Boolean = false,

    /**
     * Option to add '$exists' as query parameter
     */
    val withExists: Boolean = false,

    /**
     * Option to add '$size' as query parameter
     */
    val withSize: Boolean = false,

    /**
     * Option to add '$in' as query parameter
     */
    val withIn: Boolean = false,

    /**
     * Option to add '$all' as query parameter
     */
    val withAll: Boolean = false,

    /**
     * Option to add '$ne' as query parameter
     */
    val withNe: Boolean = false,
)
