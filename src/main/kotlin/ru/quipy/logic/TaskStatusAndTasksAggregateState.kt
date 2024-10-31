package ru.quipy.logic

import ru.quipy.api.TaskCreatedEvent
import ru.quipy.api.TaskStatusAndTasksAggregate
import ru.quipy.api.TaskStatusCreatedEvent
import ru.quipy.api.TaskUpdatedEvent
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import ru.quipy.entities.TaskEntity
import ru.quipy.entities.TaskStatusEntity
import ru.quipy.enums.StatusColor
import java.util.UUID

class TaskStatusAndTasksAggregateState: AggregateState<UUID, TaskStatusAndTasksAggregate> {

    private lateinit var id: UUID
    private lateinit var taskStatus: TaskStatusEntity
    private var tasks = mutableMapOf<UUID, TaskEntity>()

    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    override fun getId() = id

    fun getTaskStatusName() = taskStatus.name

    fun getTaskById(id: UUID) = tasks[id]

    @StateTransitionFunc
    fun statusCreatedApply(event: TaskStatusCreatedEvent) {
        id = event.statusId
        taskStatus = TaskStatusEntity(
            id = event.statusId,
            name = event.statusName,
            projectId = event.projectId,
            color = event.color,
        )
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun taskUpdatedApply(event: TaskUpdatedEvent) {
        tasks[event.taskId]?.name = event.taskName
        tasks[event.taskId]?.description = event.description
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun taskCreatedApply(event: TaskCreatedEvent) {
        tasks[event.taskId] = TaskEntity(
            id = event.taskId,
            name = event.taskName,
            description = event.description,
            projectId = event.projectId,
            statusId = event.statusId,
            assignees = event.assignees,
        )
        updatedAt = createdAt
    }
}
