package ru.quipy.projections

import org.springframework.stereotype.Service
import ru.quipy.entities.MemberEntity
import ru.quipy.projections.dto.ProjectDto
import ru.quipy.projections.dto.StatusDto
import ru.quipy.projections.dto.TaskDto
import ru.quipy.projections.dto.TaskWithMembersDto
import java.util.UUID

@Service
class Gateway (
    val statusesAndTasksProjection: StatusesAndTasksProjection,
    val projectAndMembersProjection: ProjectAndMembersProjection,
) {
    fun getAllProjects(): MutableList<ProjectDto> {
        return projectAndMembersProjection.getAllProjects().map {
            it.toDto(mutableListOf(), mutableListOf())
        }.toMutableList()
    }

    fun getAllMembersByNameSubstringNotAssignToTask(id: UUID, substring: String): MutableList<MemberEntity> {
        val task = statusesAndTasksProjection.getTaskById(id)
        val status = statusesAndTasksProjection.getStatusById(task.statusId)
        val assignees = statusesAndTasksProjection.getAllTaskAssigneesByTaskId(id)
        return projectAndMembersProjection.getAllMembersByNameSubstring(status.projectId, substring).filter {
            !assignees.contains(it.id)
        }.toMutableList()
    }

    fun getProjectWithAllStatuses(projectId: UUID): ProjectDto {
        return projectAndMembersProjection.getProjectById(projectId).toDto(
            projectAndMembersProjection.getMembersByProjectId(projectId).map {
                it.toDto()
            }.toMutableList(),
            statusesAndTasksProjection.getAllStatusesByProjectId(projectId).map {
                st -> st.toDto(statusesAndTasksProjection.getAllTasksByStatusId(st.id).map {
                    t -> t.toDto(statusesAndTasksProjection.getAllTaskAssigneesByTaskId(t.id).toMutableList())
                }.toMutableList())
            }.toMutableList()
        )
    }

    fun getTask(taskId: UUID): TaskWithMembersDto {
        val task = statusesAndTasksProjection.getTaskById(taskId)
        val assignees = statusesAndTasksProjection.getAllTaskAssigneesByTaskId(taskId)
        return statusesAndTasksProjection.getTaskById(taskId).toDto(
            projectAndMembersProjection.getAllMembersById(assignees).map { it.toDto() }.toMutableList()
        )
    }

    fun getStatus(statusId: UUID): StatusDto {
        return statusesAndTasksProjection.getStatusById(statusId).toDto(
            statusesAndTasksProjection.getAllTasksByStatusId(statusId).map {
                it.toDto(statusesAndTasksProjection.getAllTaskAssigneesByTaskId(it.id).toMutableList())
            }.toMutableList()
        )
    }
}