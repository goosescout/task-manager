package ru.quipy.logic

import ru.quipy.api.MemberCreatedEvent
import ru.quipy.api.ProjectAndMembersAggregate
import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.Aggregate
import ru.quipy.domain.AggregateState
import ru.quipy.entities.MemberEntity
import ru.quipy.entities.ProjectEntity
import java.util.UUID

class ProjectAndMembersAggregateState : AggregateState<UUID, ProjectAndMembersAggregate> {

    private lateinit var id: UUID
    private lateinit var statusesAndTasksAggregateId: UUID
    private lateinit var project: ProjectEntity
    private var members = mutableMapOf<UUID, MemberEntity>()

    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    override fun getId() = project.id

    fun getStatusesAndTasksAggregateId() = statusesAndTasksAggregateId

    fun getMemberById(id: UUID) = members[id]

    fun getName() = project.name

    fun getMembers() = members.values.toList()

    @StateTransitionFunc
    fun projectCreatedApply(event: ProjectCreatedEvent) {
        id = event.projectId
        statusesAndTasksAggregateId = event.statusesAndTasksAggregateId
        project = ProjectEntity(
            id = event.projectId,
            name = event.projectName,
        )
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun memberCreatedApply(event: MemberCreatedEvent) {
        if (members.entries.firstOrNull { it.value.userId == event.userId } != null)
            throw IllegalArgumentException("User ${event.userId} is already member of project ${event.projectId}")

        members[event.memberId] = MemberEntity(
            id = event.memberId,
            name = event.memberName,
            login = event.memberLogin,
            userId = event.userId,
            projectId = event.projectId,
        )
        updatedAt = createdAt
    }
}
