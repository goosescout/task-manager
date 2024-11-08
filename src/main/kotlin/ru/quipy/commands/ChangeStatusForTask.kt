package ru.quipy.commands

import ru.quipy.api.StatusChangedForTaskEvent
import ru.quipy.logic.TaskStatusAndTasksAggregateState
import java.util.UUID

fun TaskStatusAndTasksAggregateState.changeStatusForTask(
    taskId: UUID,
    statusId: UUID,
): StatusChangedForTaskEvent {
    if (!statuses.containsKey(statusId))
        throw NullPointerException("Status $statusId does not exist")

    if (!tasks.containsKey(taskId))
        throw NullPointerException("Task $taskId does not exist")

    return StatusChangedForTaskEvent(
        taskId = taskId,
        statusId = statusId,
    )
}
