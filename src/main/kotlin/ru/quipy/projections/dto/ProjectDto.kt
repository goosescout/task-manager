package ru.quipy.projections.dto

import java.util.UUID

data class ProjectDto (
    val id: UUID = UUID.randomUUID(),
    val name: String = "",
    val members: MutableList<MemberDto> = mutableListOf(),
    val statuses: MutableList<StatusDto> = mutableListOf(),
)