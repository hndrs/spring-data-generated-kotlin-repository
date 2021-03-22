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
     * Option to exclude field as query parameter
     */
    val withLte: Boolean = false,

    val withLt: Boolean = false,

    val withGte: Boolean = false,

    val withGt: Boolean = false,

    val withExists: Boolean = false,

    val withSize: Boolean = false,

    )
