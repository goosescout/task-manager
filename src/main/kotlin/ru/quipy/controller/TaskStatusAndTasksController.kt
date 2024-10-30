package ru.quipy.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.quipy.api.MemberCreatedEvent
import ru.quipy.api.ProjectAndMembersAggregate
import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.api.TaskStatusAndTasksAggregate
import ru.quipy.api.TaskStatusCreatedEvent
import ru.quipy.api.UserAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.enums.StatusColor
import ru.quipy.logic.ProjectAndMembersAggregateState
import ru.quipy.logic.TaskStatusAndTasksAggregateState
import ru.quipy.logic.UserAggregateState
import ru.quipy.logic.create
import ru.quipy.logic.createMember
import ru.quipy.logic.createProject
import ru.quipy.logic.createTask
import ru.quipy.logic.createTaskStatus
import java.util.UUID

@RestController
class TaskStatusAndTasksController(
    val taskEsService: EventSourcingService<UUID, TaskStatusAndTasksAggregate, TaskStatusAndTasksAggregateState>,
    val projectEsService: EventSourcingService<UUID, ProjectAndMembersAggregate, ProjectAndMembersAggregateState>,
) {

    @PostMapping("/project/{projectId}/task")
    fun createTask(
        @PathVariable projectId: UUID,
        @RequestParam statusId: UUID,
        @RequestParam name: String,
        @RequestParam description: String,
    ) : TaskCreatedEvent {
        projectEsService.getState(projectId)
            ?: throw NullPointerException("Project $projectId does not found")
        taskEsService.getState(statusId)
            ?: throw NullPointerException("Status $statusId does not found")

        return taskEsService.create { it.createTask(UUID.randomUUID(), name, description, projectId, statusId) }
    }

//  TODO: поиск таски, содержащейся в статусе
//
//    @GetMapping("/project-member/{id}")
//    fun getTask(@PathVariable id: UUID) : ProjectAndMembersAggregateState? {
//        return projectEsService.getState(id)
//    }

    @PostMapping("/project/{projectId}/task-status")
    fun createTaskStatus(
        @PathVariable projectId: UUID,
        @RequestParam name: String,
        @RequestParam color: String,
        @RequestParam position: Int?
    ) : TaskStatusCreatedEvent {
        projectEsService.getState(projectId)
            ?: throw NullPointerException("Project $projectId does not found")

        return taskEsService.create {
            it.createTaskStatus(UUID.randomUUID(), name, projectId, StatusColor.valueOf(color))
        }
    }

    @GetMapping("/task-status/{id}")
    fun getTaskStatus(@PathVariable id: UUID) : TaskStatusAndTasksAggregateState? {
        return taskEsService.getState(id)
    }

    // TODO: get all projects, search members by login/name
}