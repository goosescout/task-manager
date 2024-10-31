package ru.quipy.commands

import ru.quipy.api.StatusDeletedEvent
import ru.quipy.api.StatusPositionChangedEvent
import ru.quipy.logic.TaskStatusAndTasksAggregateState
import java.util.UUID

fun TaskStatusAndTasksAggregateState.changeTaskStatusPosition(
    projectId: UUID,
    statusId: UUID,
    position: Int,
): StatusPositionChangedEvent {
    return StatusPositionChangedEvent(
        projectId = projectId,
        statusId = statusId,
        position = position,
    )
}
