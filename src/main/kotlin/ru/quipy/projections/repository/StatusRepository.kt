package ru.quipy.projections.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.quipy.entities.TaskStatusEntity
import java.util.UUID

interface StatusRepository: JpaRepository<TaskStatusEntity, UUID> {
    fun findAllByProjectId(id: UUID): MutableList<TaskStatusEntity>
}
