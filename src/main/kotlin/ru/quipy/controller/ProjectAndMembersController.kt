package ru.quipy.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.quipy.api.MemberCreatedEvent
import ru.quipy.api.ProjectAndMembersAggregate
import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.api.UserAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.ProjectAndMembersAggregateState
import ru.quipy.logic.UserAggregateState
import ru.quipy.logic.create
import ru.quipy.logic.createMember
import ru.quipy.logic.createProject
import java.util.UUID

@RestController
class ProjectAndMembersController(
    val projectEsService: EventSourcingService<UUID, ProjectAndMembersAggregate, ProjectAndMembersAggregateState>,
    val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>
) {

    @PostMapping("/project/{projectId}/add-project-member")
    fun createMember(@PathVariable projectId: UUID, @RequestParam userId: UUID) : MemberCreatedEvent {
        val user = userEsService.getState(userId)
            ?: throw NullPointerException("User $userId does not found")

        return projectEsService.create {
            it.createMember(UUID.randomUUID(), user.getLogin(), user.getName(), user.getId(), projectId)
        }
    }

//  TODO: поиск мембера, содержащегося в проекте
//
//    @GetMapping("/project-member/{id}")
//    fun getMember(@PathVariable id: UUID) : ProjectAndMembersAggregateState? {
//        return projectEsService.getState(id)
//    }

    @PostMapping("/project/{name}")
    fun createProject(
        @PathVariable name: String,
        @RequestParam creatorId: UUID,
        @RequestParam password: String
    ) : ProjectCreatedEvent {
        val user = userEsService.getState(creatorId)
            ?: throw NullPointerException("User $creatorId does not found")

        val response = projectEsService.create { it.createProject(UUID.randomUUID(), name) }
        projectEsService.create {
            it.createMember(UUID.randomUUID(), user.getLogin(), user.getName(), user.getId(), response.projectId)
        }
        return response
    }

    @GetMapping("/project/{id}")
    fun getProject(@PathVariable id: UUID) : ProjectAndMembersAggregateState? {
        return projectEsService.getState(id)
    }

    // TODO: get all projects, search members by login/name
}