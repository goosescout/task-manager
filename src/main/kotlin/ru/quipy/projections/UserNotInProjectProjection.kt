package ru.quipy.projections

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestParam
import ru.quipy.api.MemberCreatedEvent
import ru.quipy.api.ProjectAndMembersAggregate
import ru.quipy.api.UserAggregate
import ru.quipy.api.UserCreatedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.UserAggregateState
import ru.quipy.projections.entities.MemberUserIdToProjectIdEntity
import ru.quipy.projections.entities.UserEntity
import ru.quipy.projections.repository.MemberUserIdToProjectIdRepository
import ru.quipy.projections.repository.UserRepository
import ru.quipy.streams.AggregateSubscriptionsManager
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent
import java.util.UUID
import javax.annotation.PostConstruct

@Service
@AggregateSubscriber(
        aggregateClass = UserAggregate::class, subscriberName = "user-projection"
)
class UserNotInProjectProjection(
    private val userRepository: UserRepository,
    private val memberUserIdToProjectIdRepository: MemberUserIdToProjectIdRepository,
    private val subscriptionsManager: AggregateSubscriptionsManager,
    private val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>
) {
    private val logger = LoggerFactory.getLogger(UserNotInProjectProjection::class.java)

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(UserAggregate::class, "user:user-projection") {
            `when`(UserCreatedEvent::class) { event ->
                withContext(Dispatchers.IO) {
                    userRepository.save(UserEntity(event.userId, event.userName, event.login))
                }
                logger.info("Update user projection, create user ${event.userId}")
            }
        }
        subscriptionsManager.createSubscriber(ProjectAndMembersAggregate::class, "member:user-projection") {
            `when`(MemberCreatedEvent::class) { event ->
                withContext(Dispatchers.IO) {
                    memberUserIdToProjectIdRepository.save(
                        MemberUserIdToProjectIdEntity(event.memberId, event.userId, event.projectId)
                    )
                }
                logger.info("Update user projection, create member ${event.memberId}")
            }
        }
    }

    fun getAllUsersByNameSubstringNotInProject(projectId: UUID, substring: String): MutableList<UserEntity> {
        return userRepository.findByUsernameOrLoginContaining(substring, substring).filter {
            userNotAlreadyMember(it.userId, projectId)
        }.toMutableList()
    }

    fun userNotAlreadyMember(userId: UUID, projectId: UUID): Boolean {
        return memberUserIdToProjectIdRepository.findAllByUserId(userId).none {
            it.projectId == projectId
        }
    }

    @SubscribeEvent
    fun userCreatedEventSubscriber(event: UserCreatedEvent) {
        logger.info("User created.\nId: ${event.userId}, name: ${event.userName}, login: ${event.password}")
    }

    @SubscribeEvent
    fun userCreatedEventSubscriber(event: MemberCreatedEvent) {
        logger.info("Member created.\nMember id: ${event.memberId}, user id: ${event.userId}, project id: ${event.projectId}")
    }
}
