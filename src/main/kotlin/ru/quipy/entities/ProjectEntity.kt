package ru.quipy.entities

import java.util.UUID

data class ProjectEntity(
    val id: UUID = UUID.randomUUID(),
    val name: String,
)
