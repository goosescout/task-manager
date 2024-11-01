package ru.quipy.commands

import ru.quipy.api.StatusDeletedEvent
import ru.quipy.logic.TaskStatusAndTasksAggregateState
import java.util.UUID

fun TaskStatusAndTasksAggregateState.deleteTaskStatus(
    statusId: UUID,
): StatusDeletedEvent {
    return StatusDeletedEvent(
        statusId = statusId,
    )
}
