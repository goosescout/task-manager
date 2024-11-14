package ru.quipy.projections.dto

import java.util.UUID

class TaskWithMembersDto (
    val id: UUID = UUID.randomUUID(),
    var name: String = "",
    var description: String = "",
    val assignees: MutableList<MemberDto> = mutableListOf(),
)