package ru.quipy.logic

import ru.quipy.api.UserCreatedEvent
import java.util.UUID

fun UserAggregateState.create(id: UUID, login: String, name: String, password: String): UserCreatedEvent {
    return UserCreatedEvent(
            userId = id,
            login = login,
            userName = name,
            password = password
    )
}