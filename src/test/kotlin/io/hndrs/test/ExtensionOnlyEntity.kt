package io.hndrs.test

import io.hndrs.annotation.processing.repository.GenerateRepository
import org.springframework.data.annotation.Id

@GenerateRepository(type = GenerateRepository.Type.MONGO, extensionOnly = true)
data class ExtensionOnlyEntity(
    @Id
    val id: String
)
