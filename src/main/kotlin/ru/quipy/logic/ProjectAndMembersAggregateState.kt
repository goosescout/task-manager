package ru.quipy.logic

import ru.quipy.api.MemberCreatedEvent
import ru.quipy.api.ProjectAndMembersAggregate
import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.api.TaskStatusCreatedForProjectEvent
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import ru.quipy.entities.MemberEntity
import ru.quipy.entities.ProjectEntity
import java.util.UUID

class ProjectAndMembersAggregateState : AggregateState<UUID, ProjectAndMembersAggregate> {

    private lateinit var id: UUID
    private lateinit var statusesAndTasksAggregateId: UUID
    private lateinit var project: ProjectEntity
    internal var members = mutableMapOf<UUID, MemberEntity>()

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
        project = ProjectEntity(
            id = event.projectId,
            name = event.projectName,
        )
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun memberCreatedApply(event: MemberCreatedEvent) {
        members[event.memberId] = MemberEntity(
            id = event.memberId,
            name = event.memberName,
            login = event.memberLogin,
            userId = event.userId,
            projectId = event.projectId,
        )
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun taskStatusCreatedApply(event: TaskStatusCreatedForProjectEvent) {
        if (event.projectId == project.id) {
            statusesAndTasksAggregateId = event.aggregateId
            updatedAt = event.createdAt
        }
    }
}
