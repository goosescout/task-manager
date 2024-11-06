package ru.quipy.commands

import ru.quipy.api.StatusDeletedEvent
import ru.quipy.logic.TaskStatusAndTasksAggregateState
import java.util.UUID

fun TaskStatusAndTasksAggregateState.deleteTaskStatus(
    statusId: UUID,
): StatusDeletedEvent {
    if (!statuses.containsKey(statusId))
        throw IllegalArgumentException("Status $statusId does not exist")

    if (tasks.values.any { it.statusId == statusId })
        throw IllegalStateException("Task or tasks with status $statusId exists")

    return StatusDeletedEvent(
        statusId = statusId,
    )
}
