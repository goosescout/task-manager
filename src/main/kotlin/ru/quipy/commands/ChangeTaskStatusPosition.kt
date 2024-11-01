package ru.quipy.commands

import ru.quipy.api.StatusDeletedEvent
import ru.quipy.api.StatusPositionChangedEvent
import ru.quipy.logic.TaskStatusAndTasksAggregateState
import java.util.UUID

fun TaskStatusAndTasksAggregateState.changeTaskStatusPosition(
    statusId: UUID,
    position: Int,
): StatusPositionChangedEvent {
    return StatusPositionChangedEvent(
        statusId = statusId,
        position = position,
    )
}
