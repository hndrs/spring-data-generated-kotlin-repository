package io.hndrs.sample

import io.hndrs.annotation.GenerateRepository
import io.hndrs.annotation.Options
import org.springframework.data.annotation.Id
import java.time.Instant


@GenerateRepository(extensionOnly = true)
data class User(

    @Id
    val id: String,

    @Options(updateOnSave = true)
    val name: String,

    @Options(updateOnSave = true, exclude = true)
    val createdAt: Instant,

    @Options(updateOnSave = true)
    val lastModifiedAt: Instant,
)
