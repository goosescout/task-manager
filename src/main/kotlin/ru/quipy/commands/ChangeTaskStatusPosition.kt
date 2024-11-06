package ru.quipy.commands

import ru.quipy.api.StatusDeletedEvent
import ru.quipy.api.StatusPositionChangedEvent
import ru.quipy.logic.TaskStatusAndTasksAggregateState
import java.util.UUID

fun TaskStatusAndTasksAggregateState.changeTaskStatusPosition(
    statusId: UUID,
    position: Int,
): StatusPositionChangedEvent {
    if (!statuses.containsKey(statusId))
        throw IllegalArgumentException("Status $statusId does not exist")

    if (position > statuses.size || position < 1)
        throw IllegalArgumentException("Position $position is out of bounds")

    return StatusPositionChangedEvent(
        statusId = statusId,
        position = position,
    )
}
