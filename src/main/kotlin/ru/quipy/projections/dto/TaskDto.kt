package ru.quipy.projections.dto

import java.util.UUID

class TaskDto (
    val id: UUID = UUID.randomUUID(),
    var name: String = "",
    var description: String = "",
    val assignees: MutableList<UUID> = mutableListOf(),
)
