package ru.quipy.projections.entities

import java.util.UUID
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class MemberUserIdToProjectIdEntity(
    @Id
    val memberId: UUID = UUID.randomUUID(),
    val userId: UUID = UUID.randomUUID(),
    val projectId: UUID = UUID.randomUUID(),
)
