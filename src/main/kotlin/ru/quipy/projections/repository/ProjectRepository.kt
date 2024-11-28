package ru.quipy.projections.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.quipy.entities.ProjectEntity
import java.util.UUID

interface ProjectRepository: JpaRepository<ProjectEntity, UUID>
