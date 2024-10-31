package ru.quipy.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.quipy.api.UserAggregate
import ru.quipy.api.UserCreatedEvent
import ru.quipy.entities.UserEntity
import ru.quipy.repositories.UsersRepository
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent

@Service
@AggregateSubscriber(
    aggregateClass = UserAggregate::class, subscriberName = "user-data-sub"
)
class UserViewService (
    private val userRepository: UsersRepository
){

    @SubscribeEvent
    fun saveUser(event: UserCreatedEvent) {
        userRepository.save(
            UserEntity(event.userId, event.name, event.login, event.password)
        )
    }

    fun findAllBySubstr(substr: String): List<UserEntity> {
        return userRepository.findAllBySubstr(
            buildString {
                append('%')
                append(substr)
                append('%')
            }
        )
    }
}
