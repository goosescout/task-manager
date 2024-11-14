package ru.quipy.controller

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
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
import ru.quipy.entities.MemberEntity
import ru.quipy.entities.TaskEntity
import ru.quipy.entities.TaskStatusEntity
import ru.quipy.enums.StatusColor
import ru.quipy.logic.TaskStatusAndTasksAggregateState
import ru.quipy.projections.Gateway
import ru.quipy.projections.dto.ProjectDto
import ru.quipy.projections.dto.StatusDto
import ru.quipy.projections.dto.TaskWithMembersDto
import ru.quipy.projections.entities.TaskDBEntity
import java.util.UUID

@RestController
class TaskStatusAndTasksController(
    val taskEsService: EventSourcingService<UUID, TaskStatusAndTasksAggregate, TaskStatusAndTasksAggregateState>,
    val gateway: Gateway,
) {

    @PostMapping("/tasks/{taskAggregateId}/task/create")
    fun createTask(
        @PathVariable taskAggregateId: UUID,
        @RequestParam statusId: UUID,
        @RequestParam name: String,
        @RequestParam description: String,
    ) : TaskCreatedEvent {
        return taskEsService.update(taskAggregateId) {
            it.createTask(UUID.randomUUID(), name, description, statusId)
        }
    }

    @PostMapping("/tasks/{taskAggregateId}/task/{taskId}")
    fun updateTask(
        @PathVariable taskAggregateId: UUID,
        @PathVariable taskId: UUID,
        @RequestParam name: String,
        @RequestParam description: String,
    ) : TaskUpdatedEvent {
        return taskEsService.update(taskAggregateId) {
            it.updateTask(taskId, name, description)
        }
    }

    @PostMapping("/tasks/{taskAggregateId}/task/{taskId}/change-status")
    fun changeStatusForTask(
        @PathVariable taskAggregateId: UUID,
        @PathVariable taskId: UUID,
        @RequestParam statusId: UUID,
    ) : StatusChangedForTaskEvent {
        return taskEsService.update(taskAggregateId) {
            it.changeStatusForTask(taskId, statusId)
        }
    }

    @DeleteMapping("/tasks/{taskAggregateId}/status/{statusId}")
    fun deleteTaskStatus(
        @PathVariable taskAggregateId: UUID,
        @PathVariable statusId: UUID,
    ) : StatusDeletedEvent {
        return taskEsService.update(taskAggregateId) {
            it.deleteTaskStatus(statusId)
        }
    }

    @PostMapping("/tasks/{taskAggregateId}/status/{statusId}/change-position")
    fun changeTaskStatusPosition(
        @PathVariable taskAggregateId: UUID,
        @PathVariable statusId: UUID,
        @RequestParam position: Int,
    ) : StatusPositionChangedEvent {
        return taskEsService.update(taskAggregateId) {
            it.changeTaskStatusPosition(statusId, position)
        }
    }

    @GetMapping("/tasks/{taskAggregateId}/task/{taskId}")
    fun getTask(@PathVariable taskAggregateId: UUID, @PathVariable taskId: UUID): TaskEntity? {
        return taskEsService.getState(taskAggregateId)?.getTaskById(taskId)
    }

    @GetMapping("/tasks/{taskAggregateId}/task-statuses-and-tasks")
    fun getTaskStatusesAndTasks(@PathVariable taskAggregateId: UUID) : TaskStatusAndTasksAggregateState? {
        return taskEsService.getState(taskAggregateId)
    }

    @PostMapping("/tasks/{taskAggregateId}/task-status/create")
    fun createTaskStatus(
        @PathVariable taskAggregateId: UUID,
        @RequestParam name: String,
        @RequestParam color: String,
        @RequestParam position: Int?
    ) : TaskStatusCreatedEvent {
        return taskEsService.update(taskAggregateId) {
            it.createTaskStatus(
                UUID.randomUUID(),
                name,
                taskAggregateId,
                StatusColor.valueOf(color),
                getTaskStatusesAndTasks(taskAggregateId)!!.getProjectId()
            )
        }
    }

    @GetMapping("/tasks/{taskAggregateId}/task-status/{id}")
    fun getTaskStatus(@PathVariable taskAggregateId: UUID, @PathVariable id: UUID) : TaskStatusEntity? {
        return taskEsService.getState(taskAggregateId)?.getStatusById(id)
    }

    @PostMapping("/tasks/{taskAggregateId}/task/{taskId}/add-assignee")
    fun addAssigneeForTask(
        @PathVariable taskAggregateId: UUID,
        @PathVariable taskId: UUID,
        @RequestParam memberId: UUID,
    ) : TaskAssigneeAddedEvent? {
        return taskEsService.update(taskAggregateId) {
            it.addTaskAssignee(taskId, memberId)
        }
    }

    @GetMapping("/{taskId}/find-members")
    fun findMembers(@PathVariable taskId: UUID, @RequestParam substring: String): MutableList<MemberEntity> {
        return gateway.getAllMembersByNameSubstringNotAssignToTask(taskId, substring)
    }

    @GetMapping("/task/{taskId}")
    fun getTask(@PathVariable taskId: UUID): TaskWithMembersDto {
        return gateway.getTask(taskId)
    }

    @GetMapping("/status/{statusId}")
    fun getStatus(@PathVariable statusId: UUID): StatusDto {
        return gateway.getStatus(statusId)
    }
}
