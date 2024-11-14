package ru.quipy.projections.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.quipy.entities.TaskEntity
import java.util.UUID

interface TaskRepository: JpaRepository<TaskEntity, UUID> {
    fun findAllByStatusId(id: UUID): MutableList<TaskEntity>
}
