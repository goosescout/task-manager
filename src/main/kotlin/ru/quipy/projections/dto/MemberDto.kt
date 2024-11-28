package ru.quipy.projections.dto

import java.util.UUID

data class MemberDto (
    val id: UUID = UUID.randomUUID(),
    val name: String = "",
    val login: String = "",
    val userId: UUID = UUID.randomUUID(),
)
