package ru.quipy.logic

import ru.quipy.api.MemberCreatedEvent
import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.api.TaskStatusCreatedEvent
import ru.quipy.enums.StatusColor
import java.util.UUID

fun TaskStatusAndTasksAggregateState.createTask(
    id: UUID,
    name: String,
    description: String,
    projectId: UUID,
    statusId: UUID,
): TaskCreatedEvent {
    return TaskCreatedEvent(
        taskId = id,
        taskName = name,
        description = description,
        projectId = projectId,
        statusId = statusId,
    )
}

fun TaskStatusAndTasksAggregateState.createTaskStatus(
    id: UUID,
    name: String,
    projectId: UUID,
    color: StatusColor,
): TaskStatusCreatedEvent {
    return TaskStatusCreatedEvent(
        statusId = id,
        statusName = name,
        projectId = projectId,
        color = color,
    )
}

//fun TaskStatusAndTasksAggregateState.assignTagToTask(tagId: UUID, taskId: UUID): TagAssignedToTaskEvent {
//    if (!projectTags.containsKey(tagId)) {
//        throw IllegalArgumentException("Tag doesn't exists: $tagId")
//    }
//
//    if (!tasks.containsKey(taskId)) {
//        throw IllegalArgumentException("Task doesn't exists: $taskId")
//    }
//
//    return TagAssignedToTaskEvent(projectId = this.getId(), tagId = tagId, taskId = taskId)
//}