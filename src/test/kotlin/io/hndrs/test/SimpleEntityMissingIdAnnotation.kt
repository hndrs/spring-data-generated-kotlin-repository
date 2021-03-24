package io.hndrs.test

import io.hndrs.annotation.processing.repository.GenerateRepository

@GenerateRepository(type = GenerateRepository.Type.MONGO)
data class SimpleEntityMissingIdAnnotation(
    val id: String
)
