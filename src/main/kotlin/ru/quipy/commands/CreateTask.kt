package ru.quipy.commands

import ru.quipy.api.TaskCreatedEvent
import ru.quipy.logic.TaskStatusAndTasksAggregateState
import java.util.UUID

fun TaskStatusAndTasksAggregateState.createTask(
    id: UUID,
    name: String,
    description: String,
    statusId: UUID,
): TaskCreatedEvent {
    return TaskCreatedEvent(
        taskId = id,
        taskName = name,
        description = description,
        statusId = statusId,
    )
}
