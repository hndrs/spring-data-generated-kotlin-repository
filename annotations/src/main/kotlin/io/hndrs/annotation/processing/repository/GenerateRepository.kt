package io.hndrs.annotation.processing.repository

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class GenerateRepository(
    /**
     * In case a repository already exits e.g. one with spring derived methods
     * set this value to true and implement the ExtensionRepository
     */
    val extensionOnly: Boolean = false,

    /**
     * Type of repository or extension that should be generated
     */
    val type: Type
) {
    enum class Type {
        MONGO
    }
}
