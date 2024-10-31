package ru.quipy.commands

import ru.quipy.api.StatusDeletedEvent
import ru.quipy.logic.TaskStatusAndTasksAggregateState
import java.util.UUID

fun TaskStatusAndTasksAggregateState.deleteTaskStatus(
    projectId: UUID,
    statusId: UUID,
): StatusDeletedEvent {
    return StatusDeletedEvent(
        projectId = projectId,
        statusId = statusId,
    )
}
