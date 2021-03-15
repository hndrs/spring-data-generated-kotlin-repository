package io.hndrs.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class GenerateRepository(
    /**
     * In case a repository already exits e.g. one with spring derived methods
     * set this value to true and implement the ExtensionRepository
     */
    val extensionOnly: Boolean = false
)
