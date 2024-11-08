package ru.quipy.entities

import java.util.UUID

data class MemberEntity(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val login: String,
    val userId: UUID,
    val projectId: UUID,
)
