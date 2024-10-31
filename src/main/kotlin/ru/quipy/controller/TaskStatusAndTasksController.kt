package ru.quipy.controller

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.quipy.api.ProjectAndMembersAggregate
import ru.quipy.api.StatusChangedForTaskEvent
import ru.quipy.api.StatusDeletedEvent
import ru.quipy.api.StatusPositionChangedEvent
import ru.quipy.api.TaskAssigneeAddedEvent
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.api.TaskStatusAndTasksAggregate
import ru.quipy.api.TaskStatusCreatedEvent
import ru.quipy.api.TaskUpdatedEvent
import ru.quipy.commands.addTaskAssignee
import ru.quipy.commands.changeStatusForTask
import ru.quipy.commands.changeTaskStatusPosition
import ru.quipy.commands.createTask
import ru.quipy.commands.createTaskStatus
import ru.quipy.commands.deleteTaskStatus
import ru.quipy.commands.updateTask
import ru.quipy.core.EventSourcingService
import ru.quipy.entities.TaskEntity
import ru.quipy.entities.TaskStatusEntity
import ru.quipy.enums.StatusColor
import ru.quipy.logic.ProjectAndMembersAggregateState
import ru.quipy.logic.TaskStatusAndTasksAggregateState
import java.util.UUID

@RestController
class TaskStatusAndTasksController(
    val taskEsService: EventSourcingService<UUID, TaskStatusAndTasksAggregate, TaskStatusAndTasksAggregateState>,
    val projectEsService: EventSourcingService<UUID, ProjectAndMembersAggregate, ProjectAndMembersAggregateState>,
) {

    @PostMapping("/project/{projectId}/task/create")
    fun createTask(
        @PathVariable projectId: UUID,
        @RequestParam statusId: UUID,
        @RequestParam name: String,
        @RequestParam description: String,
    ) : TaskCreatedEvent {
        projectEsService.getState(projectId)
            ?: throw NullPointerException("Project $projectId does not found")

        return taskEsService.update(projectId) {
            it.createTask(UUID.randomUUID(), name, description, projectId, statusId)
        }
    }

    @PostMapping("/project/{projectId}/task/{taskId}")
    fun updateTask(
        @PathVariable projectId: UUID,
        @PathVariable taskId: UUID,
        @RequestParam name: String,
        @RequestParam description: String,
    ) : TaskUpdatedEvent {
        taskEsService.getState(projectId)
            ?: throw NullPointerException("Project $projectId does not found")

        return taskEsService.update(projectId) { it.updateTask(taskId, projectId, name, description) }
    }

    @PostMapping("/project/{projectId}/task/{taskId}/change-status")
    fun changeStatusForTask(
        @PathVariable projectId: UUID,
        @PathVariable taskId: UUID,
        @RequestParam statusId: UUID,
    ) : StatusChangedForTaskEvent {
        taskEsService.getState(projectId)
            ?: throw NullPointerException("Project $projectId does not found")

        return taskEsService.update(projectId) { it.changeStatusForTask(taskId, projectId, statusId) }
    }

    @DeleteMapping("/project/{projectId}/status/{statusId}")
    fun deleteTaskStatus(
        @PathVariable projectId: UUID,
        @PathVariable statusId: UUID,
    ) : StatusDeletedEvent {
        return taskEsService.update(projectId) { it.deleteTaskStatus(projectId, statusId) }
    }

    @PostMapping("/project/{projectId}/status/{statusId}/change-position")
    fun changeTaskStatusPosition(
        @PathVariable projectId: UUID,
        @PathVariable statusId: UUID,
        @RequestParam position: Int,
    ) : StatusPositionChangedEvent {
        return taskEsService.update(projectId) { it.changeTaskStatusPosition(projectId, statusId, position) }
    }

    @GetMapping("/project/{projectId}/task/{taskId}")
    fun getTask(@PathVariable projectId: UUID, @PathVariable taskId: UUID): TaskEntity? {
        return taskEsService.getState(projectId)?.getTaskById(taskId)
    }

    @PostMapping("/project/{projectId}/task-status/create")
    fun createTaskStatus(
        @PathVariable projectId: UUID,
        @RequestParam name: String,
        @RequestParam color: String,
        @RequestParam position: Int?
    ) : TaskStatusCreatedEvent {
        projectEsService.getState(projectId)
            ?: throw NullPointerException("Project $projectId does not found")

        if (taskEsService.getState(projectId) == null)
            throw NullPointerException("Task statuses aggregate $projectId does not exist")

        return taskEsService.update(projectId) {
            it.createTaskStatus(UUID.randomUUID(), name, projectId, StatusColor.valueOf(color))
        }
    }

    @GetMapping("project/{projectId}/task-status/{id}")
    fun getTaskStatus(@PathVariable projectId: UUID, @PathVariable id: UUID) : TaskStatusEntity? {
        return taskEsService.getState(projectId)?.getStatusById(id)
    }

    @PostMapping("project/{projectId}/task/{statusId}/add-assignee")
    fun addAssigneeForTask(
        @PathVariable projectId: UUID,
        @PathVariable statusId: UUID,
        @RequestParam memberId: UUID,
    ) : TaskAssigneeAddedEvent? {
        val project = projectEsService.getState(projectId)
            ?: throw NullPointerException("Project $projectId does not found")

        if (project.getMemberById(memberId) == null)
            throw NullPointerException("Project $memberId does not found")

        return taskEsService.update(projectId) { it.addTaskAssignee(statusId, memberId) }
    }
}
