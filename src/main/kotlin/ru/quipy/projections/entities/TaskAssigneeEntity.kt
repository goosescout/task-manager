package ru.quipy.projections.entities

import java.util.UUID
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class TaskAssigneeEntity(
    @Id
    val id: UUID = UUID.randomUUID(),
    var taskId: UUID = UUID.randomUUID(),
    var assigneeId: UUID = UUID.randomUUID(),
)
