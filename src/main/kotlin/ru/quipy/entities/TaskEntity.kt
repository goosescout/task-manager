package ru.quipy.entities

import java.util.UUID

data class TaskEntity(
    val id: UUID = UUID.randomUUID(),
    var name: String,
    var description: String,
    val projectId: UUID,
    val assignees: MutableList<UUID>,
    var statusId: UUID,
)
