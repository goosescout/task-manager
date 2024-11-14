package ru.quipy.entities

import java.util.UUID
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class ProjectEntity(
    @Id
    val id: UUID = UUID.randomUUID(),
    val name: String = "",
)
