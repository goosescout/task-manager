package ru.quipy.commands

import ru.quipy.api.TaskUpdatedEvent
import ru.quipy.logic.TaskStatusAndTasksAggregateState
import java.util.UUID

fun TaskStatusAndTasksAggregateState.updateTask(
    id: UUID,
    name: String,
    description: String,
): TaskUpdatedEvent {
    return TaskUpdatedEvent(
        taskId = id,
        taskName = name,
        description = description,
    )
}
