package io.hndrs.test

import io.hndrs.annotation.processing.repository.GenerateRepository
import io.hndrs.annotation.processing.repository.Options
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Field
import java.time.Instant

@GenerateRepository(type = GenerateRepository.Type.MONGO)
data class AllOptionsEntity(

    @Id
    val id: String,

    @Field("optionsName")
    val fieldName: String,

    @Options(withNe = true, withAll = true, withExists = true)
    val options: String,

    @Options(withIn = true, withSize = true)
    val list: List<String>,

    @Options(withLte = true, withLt = true, withGt = true, withGte = true)
    val otherOptions: String,

    @Options(exclude = true)
    val excluded: String,

    @Options(updateOnSave = true)
    val lastModifiedAt: Instant
)
