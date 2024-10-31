package ru.quipy.entities

import java.util.UUID
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class UserEntity(
    @Id
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val login: String,
    val password: String,
)
