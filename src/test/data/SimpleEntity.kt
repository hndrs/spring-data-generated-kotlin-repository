package io.hndrs.annotation.processing.repository

import org.springframework.data.annotation.Id

@GenerateRepository(type = GenerateRepository.Type.MONGO)
data class SimpleEntity(
    @Id
    val id: String
)
