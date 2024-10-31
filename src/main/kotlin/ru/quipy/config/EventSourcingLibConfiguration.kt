package ru.quipy.config

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.quipy.api.MemberCreatedEvent
import ru.quipy.api.ProjectAndMembersAggregate
import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.api.TaskStatusAndTasksAggregate
import ru.quipy.api.TaskStatusCreatedEvent
import ru.quipy.api.TaskUpdatedEvent
import ru.quipy.api.UserAggregate
import ru.quipy.api.UserCreatedEvent
import ru.quipy.core.AggregateRegistry
import ru.quipy.core.EventSourcingServiceFactory
import ru.quipy.logic.ProjectAndMembersAggregateState
import ru.quipy.logic.TaskStatusAndTasksAggregateState
import ru.quipy.logic.UserAggregateState
import ru.quipy.projections.AnnotationBasedUserEventsSubscriber
import ru.quipy.services.UserViewService
import ru.quipy.streams.AggregateEventStreamManager
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.UUID
import javax.annotation.PostConstruct

/**
 * This files contains some configurations that you might want to have in your project. Some configurations are
 * made in for the sake of demonstration and not required for the library functioning. Usually you can have even
 * more minimalistic config
 *
 * Take into consideration that we autoscan files searching for Aggregates, Events and StateTransition functions.
 * Autoscan enabled via [event.sourcing.auto-scan-enabled] property.
 *
 * But you can always disable it and register all the classes manually like this
 * ```
 * @Autowired
 * private lateinit var aggregateRegistry: AggregateRegistry
 *
 * aggregateRegistry.register(ProjectAggregate::class, ProjectAggregateState::class) {
 *     registerStateTransition(TagCreatedEvent::class, ProjectAggregateState::tagCreatedApply)
 *     registerStateTransition(TaskCreatedEvent::class, ProjectAggregateState::taskCreatedApply)
 *     registerStateTransition(TagAssignedToTaskEvent::class, ProjectAggregateState::tagAssignedApply)
 * }
 * ```
 */
@Configuration
class EventSourcingLibConfiguration {

    private val logger = LoggerFactory.getLogger(EventSourcingLibConfiguration::class.java)


    @Autowired
    private lateinit var projectEventSubscriber: AnnotationBasedUserEventsSubscriber

    @Autowired
    private lateinit var userViewService: UserViewService

    @Autowired
    private lateinit var eventSourcingServiceFactory: EventSourcingServiceFactory

    @Autowired
    private lateinit var eventStreamManager: AggregateEventStreamManager

    @Autowired
    private lateinit var aggregateRegistry: AggregateRegistry

    @Autowired
    private lateinit var subscriptionsManager : AggregateSubscriptionsManager

    /**
     * Use this object to create/update the aggregate
     */
    @Bean
    fun userEsService() = eventSourcingServiceFactory.create<UUID, UserAggregate, UserAggregateState>()

    @Bean
    fun projectEsService() =
        eventSourcingServiceFactory.create<UUID, ProjectAndMembersAggregate, ProjectAndMembersAggregateState>()

    @Bean
    fun taskEsService() =
        eventSourcingServiceFactory.create<UUID, TaskStatusAndTasksAggregate, TaskStatusAndTasksAggregateState>()

    @PostConstruct
    fun init() {
//        aggregateRegistry.register(UserAggregate::class, UserAggregateState::class) {
//            registerStateTransition(UserCreatedEvent::class, UserAggregateState::userCreatedApply)
//        }
//        aggregateRegistry.register(TaskStatusAndTasksAggregate::class, TaskStatusAndTasksAggregateState::class) {
//            registerStateTransition(TaskCreatedEvent::class, TaskStatusAndTasksAggregateState::taskCreatedApply)
//            registerStateTransition(TaskStatusCreatedEvent::class, TaskStatusAndTasksAggregateState::statusCreatedApply)
//            registerStateTransition(TaskUpdatedEvent::class, TaskStatusAndTasksAggregateState::taskUpdatedApply)
//        }
//        aggregateRegistry.register(ProjectAndMembersAggregate::class, ProjectAndMembersAggregateState::class) {
//            registerStateTransition(ProjectCreatedEvent::class, ProjectAndMembersAggregateState::projectCreatedApply)
//            registerStateTransition(MemberCreatedEvent::class, ProjectAndMembersAggregateState::memberCreatedApply)
//        }

//        subscriptionsManager.subscribe<UserAggregate>(userViewService)

        eventStreamManager.maintenance {
            onRecordHandledSuccessfully { streamName, eventName ->
                logger.info("Stream $streamName successfully processed record of $eventName")
            }

            onBatchRead { streamName, batchSize ->
                logger.info("Stream $streamName read batch size: $batchSize")
            }
        }
    }
}
