package ru.quipy.commands

import ru.quipy.api.TaskUpdatedEvent
import ru.quipy.logic.TaskStatusAndTasksAggregateState
import java.util.UUID

fun TaskStatusAndTasksAggregateState.updateTask(
    id: UUID,
    statusId: UUID,
    name: String,
    description: String,
): TaskUpdatedEvent {
    return TaskUpdatedEvent(
        taskId = id,
        statusId = statusId,
        taskName = name,
        description = description,
    )
}
