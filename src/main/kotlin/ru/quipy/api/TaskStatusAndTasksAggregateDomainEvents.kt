package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import ru.quipy.enums.StatusColor
import java.util.UUID

const val TASK_CREATED_EVENT = "TASK_CREATED_EVENT"
const val TASK_STATUS_CREATED_EVENT = "TASK_STATUS_CREATED_EVENT"

// API
@DomainEvent(name = TASK_CREATED_EVENT)
class TaskCreatedEvent(
    val taskId: UUID,
    val taskName: String,
    val description: String,
    val projectId: UUID,
    val statusId: UUID,
    val assignees: List<UUID> = listOf(),
    createdAt: Long = System.currentTimeMillis(),
) : Event<TaskStatusAndTasksAggregate>(
    name = TASK_CREATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TASK_STATUS_CREATED_EVENT)
class TaskStatusCreatedEvent(
    val statusId: UUID,
    val statusName: String,
    val projectId: UUID,
    val color: StatusColor,
    createdAt: Long = System.currentTimeMillis(),
) : Event<TaskStatusAndTasksAggregate>(
    name = TASK_STATUS_CREATED_EVENT,
    createdAt = createdAt,
)
