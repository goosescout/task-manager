package ru.quipy.entities

import java.util.UUID
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class TaskEntity(
    @Id
    val id: UUID = UUID.randomUUID(),
    var name: String = "",
    var description: String = "",
    val assignees: MutableList<UUID> = mutableListOf(),
    var statusId: UUID = UUID.randomUUID(),
)
