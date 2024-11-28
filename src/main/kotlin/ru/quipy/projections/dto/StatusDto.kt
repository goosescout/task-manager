package ru.quipy.projections.dto

import ru.quipy.enums.StatusColor
import java.util.UUID

data class StatusDto (
    val id: UUID = UUID.randomUUID(),
    val name: String = "",
    val color: StatusColor = StatusColor.LIGHT_BLUE,
    var position: Int = 0,
    val tasks: MutableList<TaskDto> = mutableListOf(),
)
