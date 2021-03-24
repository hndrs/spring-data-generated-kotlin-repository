package io.hndrs.test

import io.hndrs.annotation.processing.repository.GenerateRepository
import io.hndrs.annotation.processing.repository.Options
import org.springframework.data.annotation.Id

@GenerateRepository(type = GenerateRepository.Type.MONGO)
data class GenericPropertiesEntity(
    @Id
    val id: String,

    @Options(withIn = true)
    val map: Map<String, Any>
)

