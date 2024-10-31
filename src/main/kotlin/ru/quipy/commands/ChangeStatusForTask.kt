package ru.quipy.commands

import ru.quipy.api.StatusChangedForTaskEvent
import ru.quipy.logic.TaskStatusAndTasksAggregateState
import java.util.UUID

fun TaskStatusAndTasksAggregateState.changeStatusForTask(
    taskId: UUID,
    projectId: UUID,
    statusId: UUID,
): StatusChangedForTaskEvent {
    return StatusChangedForTaskEvent(
        taskId = taskId,
        projectId = projectId,
        statusId = statusId,
    )
}
