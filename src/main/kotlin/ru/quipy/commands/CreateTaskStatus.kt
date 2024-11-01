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
    projectId: UUID,
    color: StatusColor,
): TaskStatusCreatedEvent {
    return TaskStatusCreatedEvent(
        statusId = statusId,
        statusName = statusName,
        aggregateId = aggregateId,
        projectId = projectId,
        color = color,
    )
}
