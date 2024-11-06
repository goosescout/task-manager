package ru.quipy.commands

import ru.quipy.api.MemberCreatedEvent
import ru.quipy.logic.ProjectAndMembersAggregateState
import java.util.UUID

fun ProjectAndMembersAggregateState.createMember(
    memberId: UUID,
    login: String,
    name: String,
    userId: UUID,
    projectId: UUID
): MemberCreatedEvent {
    if (members.entries.firstOrNull { it.value.userId == userId } != null)
        throw IllegalArgumentException("User $userId is already member of project $projectId")

    return MemberCreatedEvent(
        memberId = memberId,
        memberLogin = login,
        memberName = name,
        userId = userId,
        projectId = projectId,
    )
}
