package ru.quipy.entities

import ru.quipy.enums.StatusColor
import java.util.UUID

data class TaskStatusEntity(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val projectId: UUID,
    val color: StatusColor,
    var position: Int,
)
