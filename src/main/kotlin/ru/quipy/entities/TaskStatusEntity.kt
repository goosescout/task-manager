package ru.quipy.entities

import ru.quipy.enums.StatusColor
import java.util.UUID
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class TaskStatusEntity(
    @Id
    val id: UUID = UUID.randomUUID(),
    val name: String = "",
    val projectId: UUID = UUID.randomUUID(),
    val color: StatusColor = StatusColor.LIGHT_BLUE,
    var position: Int = 0,
)
