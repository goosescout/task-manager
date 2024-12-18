package ru.quipy.entities

import java.util.UUID
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class MemberEntity(
    @Id
    val id: UUID = UUID.randomUUID(),
    val name: String = "",
    val login: String = "",
    val userId: UUID = UUID.randomUUID(),
    val projectId: UUID = UUID.randomUUID(),
)
