package io.hndrs.sample

import io.hndrs.annotation.GenerateRepository
import io.hndrs.annotation.GenerateRepository.Type
import io.hndrs.annotation.Options
import org.springframework.data.annotation.Id
import java.time.Instant


@GenerateRepository(type = Type.MONGO)
data class User(

    @Id
    val id: String,

    @Options(updateOnSave = true)
    val name: String,

    val name1: String = "",

    val name2: String = "",

    @Options(updateOnSave = false, withLte = true, withExists = true)
    val createdAt: Instant,

    @Options(updateOnSave = true)
    val lastModifiedAt: Instant,
)
