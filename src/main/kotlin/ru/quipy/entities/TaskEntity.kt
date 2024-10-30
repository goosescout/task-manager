package ru.quipy.entities

import java.util.UUID

data class TaskEntity(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val description: String,
    val projectId: UUID,
    val assignees: List<UUID>,
    val statusId: UUID,
)
