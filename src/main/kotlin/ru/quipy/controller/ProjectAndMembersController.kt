package ru.quipy.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.quipy.api.MemberCreatedEvent
import ru.quipy.api.ProjectAndMembersAggregate
import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.api.UserAggregate
import ru.quipy.commands.createMember
import ru.quipy.commands.createProject
import ru.quipy.core.EventSourcingService
import ru.quipy.entities.MemberEntity
import ru.quipy.logic.ProjectAndMembersAggregateState
import ru.quipy.logic.UserAggregateState
import java.util.UUID

@RestController
@RequestMapping("/project")
class ProjectAndMembersController(
    val projectEsService: EventSourcingService<UUID, ProjectAndMembersAggregate, ProjectAndMembersAggregateState>,
    val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>
) {

    @PostMapping("/{projectId}/create-project-member")
    fun createMember(@PathVariable projectId: UUID, @RequestParam userId: UUID) : MemberCreatedEvent {
        val user = userEsService.getState(userId)
            ?: throw NullPointerException("User $userId does not found")

        return projectEsService.create {
            it.createMember(UUID.randomUUID(), user.getLogin(), user.getName(), user.getId(), projectId)
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
            ?: throw NullPointerException("User $creatorId does not found")

        val response = projectEsService.create { it.createProject(UUID.randomUUID(), name) }
        projectEsService.update(response.projectId) {
            it.createMember(UUID.randomUUID(), user.getLogin(), user.getName(), user.getId(), response.projectId)
        }
        return response
    }

    @GetMapping("/{id}")
    fun getProject(@PathVariable id: UUID) : ProjectAndMembersAggregateState? {
        return projectEsService.getState(id)
    }

    // TODO: get all projects, search members by login/name
}