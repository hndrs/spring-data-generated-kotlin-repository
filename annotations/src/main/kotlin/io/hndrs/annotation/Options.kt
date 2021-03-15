package io.hndrs.annotation

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
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
    val exclude: Boolean = false
)
