package ru.quipy.commands

import ru.quipy.api.UserCreatedEvent
import ru.quipy.logic.UserAggregateState
import java.util.UUID

fun UserAggregateState.createUser(id: UUID, login: String, name: String, password: String): UserCreatedEvent {
    return UserCreatedEvent(
        userId = id,
        login = login,
        userName = name,
        password = password
    )
}
