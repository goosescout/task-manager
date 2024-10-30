package ru.quipy.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.quipy.api.UserAggregate
import ru.quipy.api.UserCreatedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.UserAggregateState
import ru.quipy.logic.create
import java.util.UUID

@RestController
@RequestMapping("/users")
class UserController(
    val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>
) {
    @PostMapping("/{login}")
    fun createUser(@PathVariable login: String, @RequestParam name: String, @RequestParam password: String) : UserCreatedEvent {
        return userEsService.create { it.create(UUID.randomUUID(), login, name, password) }
    }

    @GetMapping("/{id}")
    fun getUser(@PathVariable id: UUID) : UserAggregateState? {
        return userEsService.getState(id)
    }

    // TODO: search members by login/name
//
//    @GetMapping("/search/{string}")
//    fun getUsers(@PathVariable string: String) : UserAggregateState? {
//        return projectEsService.getState(string)
//    }
}
