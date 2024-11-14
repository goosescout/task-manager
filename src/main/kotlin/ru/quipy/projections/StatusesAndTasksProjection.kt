package ru.quipy.projections

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.quipy.api.StatusChangedForTaskEvent
import ru.quipy.api.StatusDeletedEvent
import ru.quipy.api.StatusPositionChangedEvent
import ru.quipy.api.TaskAssigneeAddedEvent
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.api.TaskStatusAndTasksAggregate
import ru.quipy.api.TaskStatusCreatedEvent
import ru.quipy.api.TaskUpdatedEvent
import ru.quipy.api.UserAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.entities.TaskStatusEntity
import ru.quipy.logic.TaskStatusAndTasksAggregateState
import ru.quipy.projections.entities.TaskAssigneeEntity
import ru.quipy.projections.entities.TaskDBEntity
import ru.quipy.projections.repository.StatusRepository
import ru.quipy.projections.repository.TaskAssigneeRepository
import ru.quipy.projections.repository.TaskRepository
import ru.quipy.streams.AggregateSubscriptionsManager
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent
import java.util.UUID
import javax.annotation.PostConstruct

@Service
@AggregateSubscriber(
        aggregateClass = UserAggregate::class, subscriberName = "status-projection"
)
class StatusesAndTasksProjection(
    private val statusRepository: StatusRepository,
    private val taskRepository: TaskRepository,
    private val taskAssigneeRepository: TaskAssigneeRepository,
    private val subscriptionsManager: AggregateSubscriptionsManager,
    val taskEsService: EventSourcingService<UUID, TaskStatusAndTasksAggregate, TaskStatusAndTasksAggregateState>,
) {
    private val logger = LoggerFactory.getLogger(StatusesAndTasksProjection::class.java)

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(TaskStatusAndTasksAggregate::class, "status:status-projection") {
            `when`(TaskCreatedEvent::class) { event ->
                withContext(Dispatchers.IO) {
                    taskRepository.save(TaskDBEntity(event.taskId, event.taskName, event.description, event.statusId))
                }
                logger.info("Update status projection, create task ${event.taskId}")
            }
            `when`(TaskUpdatedEvent::class) { event ->
                withContext(Dispatchers.IO) {
                    val task = taskRepository.getReferenceById(event.taskId)
                    task.name = event.taskName
                    task.description = event.description
                    taskRepository.save(task)
                }
                logger.info("Update status projection, update task ${event.taskId}")
            }
            `when`(StatusChangedForTaskEvent::class) { event ->
                withContext(Dispatchers.IO) {
                    val task = taskRepository.getReferenceById(event.taskId)
                    task.statusId = event.statusId
                    taskRepository.save(task)
                }
                logger.info("Update status projection, change status for task ${event.taskId}")
            }
            `when`(TaskAssigneeAddedEvent::class) { event ->
                withContext(Dispatchers.IO) {
                    taskAssigneeRepository.save(TaskAssigneeEntity(UUID.randomUUID(), event.taskId, event.memberId))
                }
                logger.info("Update status projection, add assignee to task ${event.taskId}")
            }
            `when`(TaskStatusCreatedEvent::class) { event ->
                withContext(Dispatchers.IO) {
                    statusRepository.save(TaskStatusEntity(event.statusId, event.statusName, event.projectId, event.color, getAllStatusesByProjectId(event.projectId).size + 1))
                }
                logger.info("Update status projection, create user ${event.statusId}")
            }
            `when`(StatusDeletedEvent::class) { event ->
                withContext(Dispatchers.IO) {
                    taskRepository.deleteById(event.statusId)
                }
                logger.info("Update status projection, delete status ${event.statusId}")
            }
            `when`(StatusPositionChangedEvent::class) { event ->
                withContext(Dispatchers.IO) {
                    val status = statusRepository.getReferenceById(event.statusId)
                    status.position = event.position
                    statusRepository.save(status)
                }
                logger.info("Update status projection, change status position ${event.statusId}")
            }
        }
    }

    fun getAllStatusesById(ids: List<UUID>): List<TaskStatusEntity> {
        return statusRepository.findAllById(ids)
    }

    fun getAllTasksById(ids: List<UUID>): List<TaskDBEntity> {
        return taskRepository.findAllById(ids)
    }

    fun getAllTaskAssigneesByTaskId(id: UUID): List<UUID> {
        return taskAssigneeRepository.findAllByTaskId(id).map { it.assigneeId }
    }

    fun getAllStatusesByProjectId(id: UUID): List<TaskStatusEntity> {
        return statusRepository.findAllByProjectId(id)
    }

    fun getAllTasksByStatusId(id: UUID): List<TaskDBEntity> {
        return taskRepository.findAllByStatusId(id)
    }

    fun getTaskById(id: UUID): TaskDBEntity {
        return taskRepository.findById(id).get()
    }

    fun getStatusById(id: UUID): TaskStatusEntity {
        return statusRepository.findById(id).get()
    }

    @SubscribeEvent
    fun taskCreatedEventEventSubscriber(event: TaskCreatedEvent) {
        logger.info("Task created.\nId: ${event.taskId}, name: ${event.taskName}, description: ${event.description}, statusId: ${event.statusId}")
    }

    @SubscribeEvent
    fun taskUpdatedEventEventSubscriber(event: TaskUpdatedEvent) {
        logger.info("Task updated.\nId: ${event.taskId}, new name: ${event.taskName}, new description: ${event.description}")
    }

    @SubscribeEvent
    fun statusChangedForTaskEventSubscriber(event: StatusChangedForTaskEvent) {
        logger.info("Task status changed.\nId: ${event.taskId}, new status id: ${event.statusId}")
    }

    @SubscribeEvent
    fun taskAssigneeAddedEventSubscriber(event: TaskAssigneeAddedEvent) {
        logger.info("Add assignee to task.\nTask id: ${event.taskId}, new assignee id: ${event.memberId}")
    }

    @SubscribeEvent
    fun taskStatusCreatedEventSubscriber(event: TaskStatusCreatedEvent) {
        logger.info("Status created.\nId: ${event.statusId}, name: ${event.statusName}, color: ${event.color}, projectId: ${event.projectId}")
    }

    @SubscribeEvent
    fun statusDeletedEventSubscriber(event: StatusDeletedEvent) {
        logger.info("Status deleted.\nId: ${event.statusId}")
    }

    @SubscribeEvent
    fun statusPositionChangedEventSubscriber(event: StatusPositionChangedEvent) {
        logger.info("Status position changed.\nId: ${event.statusId}, new position: ${event.position}")
    }
}
