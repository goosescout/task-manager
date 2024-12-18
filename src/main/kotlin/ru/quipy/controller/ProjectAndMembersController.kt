package ru.quipy.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.quipy.api.*
import ru.quipy.commands.addStatusAggregateId
import ru.quipy.commands.createMember
import ru.quipy.commands.createProject
import ru.quipy.commands.createTaskStatus
import ru.quipy.core.EventSourcingService
import ru.quipy.entities.MemberEntity
import ru.quipy.enums.StatusColor
import ru.quipy.logic.ProjectAndMembersAggregateState
import ru.quipy.logic.TaskStatusAndTasksAggregateState
import ru.quipy.logic.UserAggregateState
import ru.quipy.projections.Gateway
import ru.quipy.projections.UserNotInProjectProjection
import ru.quipy.projections.dto.ProjectDto
import ru.quipy.projections.entities.UserEntity
import java.util.UUID

@RestController
@RequestMapping("/project")
class ProjectAndMembersController(
    val projectEsService: EventSourcingService<UUID, ProjectAndMembersAggregate, ProjectAndMembersAggregateState>,
    val taskEsService: EventSourcingService<UUID, TaskStatusAndTasksAggregate, TaskStatusAndTasksAggregateState>,
    val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>,
    val userProjection: UserNotInProjectProjection,
    val gateway: Gateway,
) {

    @PostMapping("/{projectId}/create-project-member")
    fun createMember(@PathVariable projectId: UUID, @RequestParam userId: UUID) : MemberCreatedEvent {
        val user = userEsService.getState(userId)

        return projectEsService.update(projectId) {
            it.createMember(UUID.randomUUID(), user?.getLogin(), user?.getName(), user?.getId(), projectId)
        }
    }

    @GetMapping("/{projectId}/member/{memberId}")
    fun getMember(@PathVariable projectId: UUID, @PathVariable memberId: UUID) : MemberEntity? {
        return projectEsService.getState(projectId)?.getMemberById(memberId)
    }

    @PostMapping("/create")
    fun createProject(
        @RequestParam name: String,
        @RequestParam creatorId: UUID,
    ) : ProjectCreatedEvent {
        val user = userEsService.getState(creatorId)

        val response = projectEsService.create { it.createProject(UUID.randomUUID(), name) }

        val taskResponse = taskEsService.create {
            it.createTaskStatus(UUID.randomUUID(), "CREATED", UUID.randomUUID(), StatusColor.GREEN, response.projectId)
        }
        projectEsService.update(response.projectId) {
            it.createMember(UUID.randomUUID(), user?.getLogin(), user?.getName(), user?.getId(), response.projectId)
        }
        projectEsService.update(response.projectId) {
            it.addStatusAggregateId(response.projectId, taskResponse.aggregateId)
        }

        return response
    }

    @GetMapping("/{id}")
    fun getProject(@PathVariable id: UUID) : ProjectAndMembersAggregateState? {
        return projectEsService.getState(id)
    }

    @GetMapping("/{id}/find-users")
    fun findUsers(@PathVariable id: UUID, @RequestParam substring: String): MutableList<UserEntity> {
        return userProjection.getAllUsersByNameSubstringNotInProject(id, substring)
    }

    @GetMapping("/get-all-projects")
    fun getAllProjects(): MutableList<ProjectDto> {
        return gateway.getAllProjects()
    }

    @GetMapping("/{projectId}/get-all-statuses")
    fun getProjectWithAllStatuses(@PathVariable projectId: UUID): ProjectDto {
        return gateway.getProjectWithAllStatuses(projectId)
    }
}