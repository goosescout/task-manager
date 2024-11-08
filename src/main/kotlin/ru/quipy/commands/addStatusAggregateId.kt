package ru.quipy.commands

import ru.quipy.api.TaskStatusCreatedForProjectEvent
import ru.quipy.logic.ProjectAndMembersAggregateState
import java.util.UUID

fun ProjectAndMembersAggregateState.addStatusAggregateId(
    projectId: UUID,
    statusAggregateId: UUID,
): TaskStatusCreatedForProjectEvent {
    return TaskStatusCreatedForProjectEvent(
        projectId = projectId,
        aggregateId = statusAggregateId,
    )
}