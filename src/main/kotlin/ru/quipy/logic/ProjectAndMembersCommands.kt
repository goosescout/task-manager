package ru.quipy.logic

import ru.quipy.api.MemberCreatedEvent
import ru.quipy.api.ProjectCreatedEvent
import java.util.UUID

fun ProjectAndMembersAggregateState.createProject(id: UUID, name: String): ProjectCreatedEvent {
    return ProjectCreatedEvent(
        projectId = id,
        projectName = name,
    )
}

fun ProjectAndMembersAggregateState.createMember(
    memberId: UUID,
    login: String,
    name: String,
    userId: UUID,
    projectId: UUID
): MemberCreatedEvent {
    return MemberCreatedEvent(
        memberId = memberId,
        memberLogin = login,
        memberName = name,
        userId = userId,
        projectId = projectId,
    )
}
