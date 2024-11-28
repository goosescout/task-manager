package ru.quipy.projections.entities

import java.util.UUID
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class TaskDBEntity(
    @Id
    val id: UUID = UUID.randomUUID(),
    var name: String = "",
    var description: String = "",
    var statusId: UUID = UUID.randomUUID(),
)
