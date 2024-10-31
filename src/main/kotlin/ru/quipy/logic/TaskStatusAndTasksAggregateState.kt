package ru.quipy.logic

import ru.quipy.api.StatusChangedForTaskEvent
import ru.quipy.api.StatusDeletedEvent
import ru.quipy.api.StatusPositionChangedEvent
import ru.quipy.api.TaskAssigneeAddedEvent
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.api.TaskStatusAndTasksAggregate
import ru.quipy.api.TaskStatusCreatedEvent
import ru.quipy.api.TaskUpdatedEvent
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import ru.quipy.entities.TaskEntity
import ru.quipy.entities.TaskStatusEntity
import java.util.UUID

class TaskStatusAndTasksAggregateState: AggregateState<UUID, TaskStatusAndTasksAggregate> {

    private lateinit var id: UUID
    private var statuses = mutableMapOf<UUID, TaskStatusEntity>()
    private var tasks = mutableMapOf<UUID, TaskEntity>()

    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    override fun getId() = id

    fun getTaskById(id: UUID) = tasks[id]

    fun getStatusById(id: UUID) = statuses[id]

    @StateTransitionFunc
    fun statusCreatedApply(event: TaskStatusCreatedEvent) {
        id = event.projectId
        statuses[event.statusId] = TaskStatusEntity(
            id = event.statusId,
            name = event.statusName,
            color = event.color,
            projectId = event.projectId,
            position = statuses.size + 1
        )
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun statusDeletedApply(event: StatusDeletedEvent) {
        if (tasks.values.any { it.statusId == event.statusId })
            throw IllegalStateException("Task or tasks with status ${event.statusId} exists")

        val status = statuses[event.statusId] ?: throw NullPointerException("Status ${event.statusId} does not exist")

        statuses.entries.forEach {
            if (it.value.position > status.position) {
                val tmp = it.value
                tmp.position -= 1
                statuses[it.key] = tmp
            }
        }

        statuses.remove(event.statusId)
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun statusPositionChangedApply(event: StatusPositionChangedEvent) {
        val status = statuses[event.statusId] ?: throw NullPointerException("Status ${event.statusId} does not exist")

        val oldPosition = status.position

        if (event.position > statuses.size || event.position < 1)
            throw IllegalArgumentException("Position ${event.position} out of bound")

        if (event.position < oldPosition) {
            statuses.entries.forEach {
                if (it.value.position > event.position && it.value.position <= oldPosition) {
                    val tmp = it.value
                    tmp.position -= 1
                    statuses[it.key] = tmp
                }
            }
        } else {
            statuses.entries.forEach {
                if (it.value.position < event.position && it.value.position >= oldPosition) {
                    val tmp = it.value
                    tmp.position -= 1
                    statuses[it.key] = tmp
                }
            }
        }

        status.position = event.position
        statuses[event.statusId] = status
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun statusChangedForTaskEventApply(event: StatusChangedForTaskEvent) {
        if (!statuses.containsKey(event.statusId))
            throw NullPointerException("Status ${event.statusId} does not exist")

        if (!tasks.containsKey(event.taskId))
            throw NullPointerException("Task ${event.taskId} does not exist")

        tasks[event.taskId]?.statusId = event.statusId
        updatedAt = event.createdAt
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

    @StateTransitionFunc
    fun taskAssigneeAddedEventApply(event: TaskAssigneeAddedEvent) {
        val task = tasks[event.taskId] ?: throw NullPointerException("Task ${event.taskId} does not exist")
        if (task.assignees.contains(event.memberId))
            throw IllegalArgumentException("Member ${event.memberId} already assigned to task ${event.taskId}")

        tasks[event.taskId]!!.assignees.add(event.memberId)
        updatedAt = createdAt
    }
}
