package ru.quipy.commands

import ru.quipy.api.TaskAssigneeAddedEvent
import ru.quipy.logic.TaskStatusAndTasksAggregateState
import java.util.UUID

fun TaskStatusAndTasksAggregateState.addTaskAssignee(
    taskId: UUID,
    memberId: UUID,
): TaskAssigneeAddedEvent {
    return TaskAssigneeAddedEvent(
        taskId = taskId,
        memberId = memberId,
    )
}
