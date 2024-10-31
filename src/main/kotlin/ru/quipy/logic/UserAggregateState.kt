package ru.quipy.logic

import ru.quipy.api.UserAggregate
import ru.quipy.api.UserCreatedEvent
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.UUID

class UserAggregateState : AggregateState<UUID, UserAggregate> {
    private lateinit var id: UUID
    private lateinit var login: String
    private lateinit var name: String
    private lateinit var password: String

    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    override fun getId() = id

    fun getLogin() = login

    fun getName() = name

    fun getPassword() = password

    @StateTransitionFunc
    fun userCreatedApply(event: UserCreatedEvent) {
        id = event.userId
        name = event.userName
        login = event.login
        password = event.password
        updatedAt = createdAt
    }
}
