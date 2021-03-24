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

    @Options(withNe = true, withIn = true, withExists = true)
    val options: String,

    @Options(withSize = true, withAll = true)
    val listVar: List<String>,

    @Options(withLte = true, withLt = true, withGt = true, withGte = true)
    val otherOptions: String,

    @Options(exclude = true)
    val excluded: String,

    val boolean: Boolean = true,

    @Options(withExists = true)
    val int: Int = Int.MIN_VALUE,

    @Options(withExists = true)
    val float: Float = Float.MIN_VALUE,

    @Options(withExists = true)
    val byte: Byte = Byte.MIN_VALUE,

    @Options(withExists = true)
    val short: Short = Short.MIN_VALUE,

    @Options(withExists = true)
    val long: Long = Long.MIN_VALUE,

    @Options(withExists = true)
    val char: Char = Char.MIN_VALUE,

    @Options(withExists = true)
    var double: Double = Double.MIN_VALUE,

    @Options(updateOnSave = true)
    val lastModifiedAt: Instant
)
