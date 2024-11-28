package ru.quipy.projections.entities

import java.util.UUID
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class UserEntity(
    @Id
    val userId: UUID = UUID.randomUUID(),
    var username: String = "",
    var login: String = "",
)