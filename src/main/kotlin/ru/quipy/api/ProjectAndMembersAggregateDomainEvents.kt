package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.UUID

const val PROJECT_CREATED_EVENT = "PROJECT_CREATED_EVENT"
const val MEMBER_CREATED_EVENT = "MEMBER_CREATED_EVENT"

// API
@DomainEvent(name = PROJECT_CREATED_EVENT)
class ProjectCreatedEvent(
    val projectId: UUID,
    val statusesAndTasksAggregateId: UUID,
    val projectName: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAndMembersAggregate>(
    name = PROJECT_CREATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = MEMBER_CREATED_EVENT)
class MemberCreatedEvent(
    val memberId: UUID,
    val memberLogin: String,
    val memberName: String,
    val userId: UUID,
    val projectId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAndMembersAggregate>(
    name = MEMBER_CREATED_EVENT,
    createdAt = createdAt,
)
