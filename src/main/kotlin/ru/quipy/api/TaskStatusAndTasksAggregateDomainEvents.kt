package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import ru.quipy.enums.StatusColor
import java.util.UUID

const val TASK_CREATED_EVENT = "TASK_CREATED_EVENT"
const val TASK_UPDATED_EVENT = "TASK_UPDATED_EVENT"
const val TASK_DELETED_EVENT = "TASK_DELETED_EVENT"
const val TASK_ASSINGNEE_ADDED_EVENT = "TASK_ASSINGNEE_ADDED_EVENT"
const val TASK_STATUS_CREATED_EVENT = "TASK_STATUS_CREATED_EVENT"
const val TASK_STATUS_POSITION_CHANGED_EVENT = "TASK_STATUS_POSITION_CHANGED_EVENT"
const val STATUS_CHANGED_FOR_TASK_EVENT = "STATUS_CHANGED_FOR_TASK_EVENT"

// API
@DomainEvent(name = TASK_CREATED_EVENT)
class TaskCreatedEvent(
    val taskId: UUID,
    val taskName: String,
    val description: String,
    val projectId: UUID,
    val statusId: UUID,
    val assignees: MutableList<UUID> = mutableListOf(),
    createdAt: Long = System.currentTimeMillis(),
) : Event<TaskStatusAndTasksAggregate>(
    name = TASK_CREATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TASK_DELETED_EVENT)
class StatusDeletedEvent(
    val statusId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<TaskStatusAndTasksAggregate>(
    name = TASK_UPDATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TASK_STATUS_POSITION_CHANGED_EVENT)
class StatusPositionChangedEvent(
    val statusId: UUID,
    val position: Int,
    createdAt: Long = System.currentTimeMillis(),
) : Event<TaskStatusAndTasksAggregate>(
    name = TASK_STATUS_POSITION_CHANGED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TASK_UPDATED_EVENT)
class TaskUpdatedEvent(
    val taskId: UUID,
    val taskName: String,
    val description: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<TaskStatusAndTasksAggregate>(
    name = TASK_UPDATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TASK_STATUS_CREATED_EVENT)
class TaskStatusCreatedEvent(
    val statusId: UUID,
    val statusName: String,
    val aggregateId: UUID,
    val projectId: UUID,
    val color: StatusColor,
    createdAt: Long = System.currentTimeMillis(),
) : Event<TaskStatusAndTasksAggregate>(
    name = TASK_STATUS_CREATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = STATUS_CHANGED_FOR_TASK_EVENT)
class StatusChangedForTaskEvent(
    val taskId: UUID,
    val statusId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<TaskStatusAndTasksAggregate>(
    name = TASK_UPDATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TASK_ASSINGNEE_ADDED_EVENT)
class TaskAssigneeAddedEvent(
    val taskId: UUID,
    val memberId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<TaskStatusAndTasksAggregate>(
    name = TASK_UPDATED_EVENT,
    createdAt = createdAt,
)
