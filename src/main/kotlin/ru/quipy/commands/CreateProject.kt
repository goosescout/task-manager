package ru.quipy.commands

import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.logic.ProjectAndMembersAggregateState
import java.util.UUID

fun ProjectAndMembersAggregateState.createProject(id: UUID, name: String): ProjectCreatedEvent {
    return ProjectCreatedEvent(
        projectId = id,
        projectName = name,
    )
}
