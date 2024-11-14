package ru.quipy.projections.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.quipy.projections.entities.TaskAssigneeEntity
import java.util.UUID

interface TaskAssigneeRepository: JpaRepository<TaskAssigneeEntity, UUID> {
    fun findAllByTaskId(id: UUID): MutableList<TaskAssigneeEntity>
}
