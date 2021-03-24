package io.hndrs.test

import io.hndrs.annotation.processing.repository.GenerateRepository
import org.springframework.data.annotation.Id

/**
 * Represents a normal wrapped class
 */
class AnyWrapper {

    @GenerateRepository(type = GenerateRepository.Type.MONGO)
    data class NestedSimpleEntity(
        @Id
        val id: String
    )
}

