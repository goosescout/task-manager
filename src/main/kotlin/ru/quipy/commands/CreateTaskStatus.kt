package ru.quipy.commands

import ru.quipy.api.TaskStatusCreatedEvent
import ru.quipy.domain.Aggregate
import ru.quipy.enums.StatusColor
import ru.quipy.logic.TaskStatusAndTasksAggregateState
import java.util.UUID

fun TaskStatusAndTasksAggregateState.createTaskStatus(
    statusId: UUID,
    statusName: String,
    aggregateId: UUID,
    color: StatusColor,
    projectId: UUID
): TaskStatusCreatedEvent {
    return TaskStatusCreatedEvent(
        statusId = statusId,
        statusName = statusName,
        aggregateId = aggregateId,
        color = color,
        projectId = projectId,
    )
}
