package ru.quipy.commands

import ru.quipy.api.TaskStatusCreatedEvent
import ru.quipy.enums.StatusColor
import ru.quipy.logic.TaskStatusAndTasksAggregateState
import java.util.UUID

fun TaskStatusAndTasksAggregateState.createTaskStatus(
    id: UUID,
    name: String,
    projectId: UUID,
    color: StatusColor,
): TaskStatusCreatedEvent {
    return TaskStatusCreatedEvent(
        statusId = id,
        statusName = name,
        projectId = projectId,
        color = color,
    )
}
