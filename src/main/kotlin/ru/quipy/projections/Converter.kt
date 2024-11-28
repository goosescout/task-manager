package ru.quipy.projections

import ru.quipy.entities.MemberEntity
import ru.quipy.entities.ProjectEntity
import ru.quipy.entities.TaskStatusEntity
import ru.quipy.projections.dto.MemberDto
import ru.quipy.projections.dto.ProjectDto
import ru.quipy.projections.dto.StatusDto
import ru.quipy.projections.dto.TaskDto
import ru.quipy.projections.dto.TaskWithMembersDto
import ru.quipy.projections.entities.TaskDBEntity
import java.util.UUID

fun ProjectEntity.toDto(members: MutableList<MemberDto>, statuses: MutableList<StatusDto>): ProjectDto {
    return ProjectDto(
        id = this.id,
        name = this.name,
        members = members,
        statuses = statuses,
    )
}

fun MemberEntity.toDto(): MemberDto {
    return MemberDto(
        id = this.id,
        name = this.name,
        login = this.login,
        userId = this.userId,
    )
}

fun TaskStatusEntity.toDto(tasks: MutableList<TaskDto>): StatusDto {
    return StatusDto(
        id = this.id,
        name = this.name,
        color = this.color,
        position = this.position,
        tasks = tasks,
    )
}

fun TaskDBEntity.toDto(assignees: MutableList<UUID>): TaskDto {
    return TaskDto(
        id = this.id,
        name = this.name,
        description = this.description,
        assignees = assignees,
    )
}

fun TaskDBEntity.toDto(members: MutableList<MemberDto>): TaskWithMembersDto {
    return TaskWithMembersDto(
        id = this.id,
        name = this.name,
        description = this.description,
        assignees = members,
    )
}
