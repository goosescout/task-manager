package ru.quipy.projections.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.quipy.projections.entities.TaskDBEntity
import java.util.UUID

interface TaskRepository: JpaRepository<TaskDBEntity, UUID> {
    fun findAllByStatusId(id: UUID): MutableList<TaskDBEntity>
}
