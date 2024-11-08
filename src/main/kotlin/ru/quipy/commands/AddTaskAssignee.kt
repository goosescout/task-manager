package ru.quipy.commands

import ru.quipy.api.TaskAssigneeAddedEvent
import ru.quipy.logic.TaskStatusAndTasksAggregateState
import java.util.UUID

fun TaskStatusAndTasksAggregateState.addTaskAssignee(
    taskId: UUID,
    memberId: UUID,
): TaskAssigneeAddedEvent {
    val task = tasks[taskId] ?: throw NullPointerException("Task $taskId does not exist")
    if (task.assignees.contains(memberId))
        throw IllegalArgumentException("Member $memberId is already assigned to task $taskId")

    return TaskAssigneeAddedEvent(
        taskId = taskId,
        memberId = memberId,
    )
}
