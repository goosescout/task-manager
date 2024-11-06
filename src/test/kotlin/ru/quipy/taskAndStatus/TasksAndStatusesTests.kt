package ru.quipy.taskAndStatus

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.quipy.api.TaskStatusCreatedEvent
import ru.quipy.api.UserCreatedEvent
import ru.quipy.controller.ProjectAndMembersController
import ru.quipy.controller.TaskStatusAndTasksController
import ru.quipy.controller.UserController
import ru.quipy.entities.TaskStatusEntity
import ru.quipy.enums.StatusColor
import ru.quipy.logic.ProjectAndMembersAggregateState
import java.util.UUID

@SpringBootTest
class TasksAndStatusesTests {

	@Autowired
	private lateinit var userController: UserController

	@Autowired
	private lateinit var projectController: ProjectAndMembersController

	@Autowired
	private lateinit var taskController: TaskStatusAndTasksController

	@Test
	fun tasksAndStatusesHappyPath() {
		val owner = createUser("Owner")
		val user = createUser("Member")

		val project = createProjectWithMember(owner.userId, user.userId)

		Thread.sleep(500)

		val taskAggregate = taskController.getTaskStatusesAndTasks(project!!.getStatusesAndTasksAggregateId())

		Assertions.assertNotNull(taskAggregate)
		Assertions.assertEquals(1, taskAggregate!!.getStatuses().size)
		Assertions.assertEquals("CREATED", taskAggregate.getStatuses()[0].name)
		Assertions.assertEquals(StatusColor.GREEN, taskAggregate.getStatuses()[0].color)
		Assertions.assertEquals(1, taskAggregate.getStatuses()[0].position)
		Assertions.assertEquals(0, taskAggregate.getTasks().size)

		val defStatus = taskController.getTaskStatus(taskAggregate.getId(), taskAggregate.getStatuses()[0].id)
		Assertions.assertNotNull(defStatus)
		Assertions.assertEquals("CREATED", defStatus!!.name)
		Assertions.assertEquals(StatusColor.GREEN, defStatus.color)
		Assertions.assertEquals(1, defStatus.position)

		val newStatus = taskController.createTaskStatus(
			taskAggregate.getId(),
			"Completed",
			"ORANGE",
			null
		)
		val newStatusAgg = taskController.getTaskStatus(taskAggregate.getId(), newStatus.statusId)
		Assertions.assertNotNull(newStatusAgg)
		Assertions.assertEquals("Completed", newStatusAgg!!.name)
		Assertions.assertEquals(StatusColor.ORANGE, newStatusAgg.color)
		Assertions.assertEquals(2, newStatusAgg.position)

		val task = taskController.createTask(
			taskAggregate.getId(),
			newStatus.statusId,
			"Task",
			"Description1"
		)
		val taskAgg = taskController.getTask(taskAggregate.getId(), task.taskId)
		Assertions.assertNotNull(taskAgg)
		Assertions.assertEquals("Task", taskAgg!!.name)
		Assertions.assertEquals("Description1", taskAgg.description)
		Assertions.assertEquals(0, taskAgg.assignees.size)
		Assertions.assertEquals(newStatus.statusId, taskAgg.statusId)
		Assertions.assertEquals(project.getId(), taskAgg.projectId)

		Assertions.assertThrows(
			IllegalStateException::class.java
		) {
			taskController.deleteTaskStatus(taskAggregate.getId(), newStatus.statusId)
		}

		val taskNewStatus = taskController.changeStatusForTask(
			taskAggregate.getId(),
			task.taskId,
			defStatus.id
		)
		val taskAggNewStatus = taskController.getTask(taskAggregate.getId(), taskNewStatus.taskId)
		Assertions.assertNotNull(taskAggNewStatus)
		Assertions.assertEquals("Task", taskAggNewStatus!!.name)
		Assertions.assertEquals(defStatus.id, taskAggNewStatus.statusId)

		val taskNewNameDescription = taskController.updateTask(
			taskAggregate.getId(),
			taskAgg.id,
			"newNameTask",
			"newDescription"
		)
		val taskAggNewNameDescription = taskController.getTask(taskAggregate.getId(), taskNewNameDescription.taskId)
		Assertions.assertNotNull(taskAggNewNameDescription)
		Assertions.assertEquals("newNameTask", taskAggNewNameDescription!!.name)
		Assertions.assertEquals("newDescription", taskAggNewNameDescription.description)
		Assertions.assertEquals(defStatus.id, taskAggNewStatus.statusId)

		taskController.deleteTaskStatus(taskAggregate.getId(), newStatus.statusId)
		val agg = taskController.getTaskStatusesAndTasks(taskAggregate.getId())
		Assertions.assertNotNull(agg)
		Assertions.assertEquals(1, agg!!.getStatuses().size)
		Assertions.assertEquals("CREATED", agg.getStatuses()[0].name)
		Assertions.assertEquals(StatusColor.GREEN, agg.getStatuses()[0].color)
		Assertions.assertEquals(1, agg.getStatuses()[0].position)
		Assertions.assertEquals(1, agg.getTasks().size)
	}


	@Test
	fun statusesSwapPositionsHappyPath() {
		val owner = createUser("Owner")
		val user = createUser("Member")
		val project = createProjectWithMember(owner.userId, user.userId)

		val zeroId = taskController
			.getTaskStatusesAndTasks(project!!.getStatusesAndTasksAggregateId())!!
			.getStatuses()[0].id

		val firstId = createStatus(
			project.getStatusesAndTasksAggregateId(),
			firstStatus
		).statusId
		val secondId = createStatus(
			project.getStatusesAndTasksAggregateId(),
			secondStatus
		).statusId
		val thirdId = createStatus(
			project.getStatusesAndTasksAggregateId(),
			thirdStatus
		).statusId

		val taskAggregate = taskController.getTaskStatusesAndTasks(project.getStatusesAndTasksAggregateId())
		Assertions.assertEquals(4, taskAggregate!!.getStatuses().size)
		validateStatus(taskAggregate.getId(), zeroId, zeroStatus, 1)
		validateStatus(taskAggregate.getId(), firstId, firstStatus, 2)
		validateStatus(taskAggregate.getId(), secondId, secondStatus, 3)
		validateStatus(taskAggregate.getId(), thirdId, thirdStatus, 4)

		taskController.changeTaskStatusPosition(
			taskAggregate.getId(),
			firstId,
			4
		)
		validateStatus(taskAggregate.getId(), zeroId, zeroStatus, 1)
		validateStatus(taskAggregate.getId(), firstId, firstStatus, 4)
		validateStatus(taskAggregate.getId(), secondId, secondStatus, 2)
		validateStatus(taskAggregate.getId(), thirdId, thirdStatus, 3)

		taskController.changeTaskStatusPosition(
			taskAggregate.getId(),
			thirdId,
			1
		)
		validateStatus(taskAggregate.getId(), zeroId, zeroStatus, 2)
		validateStatus(taskAggregate.getId(), firstId, firstStatus, 4)
		validateStatus(taskAggregate.getId(), secondId, secondStatus, 3)
		validateStatus(taskAggregate.getId(), thirdId, thirdStatus, 1)

		taskController.deleteTaskStatus(taskAggregate.getId(), thirdId)
		validateStatus(taskAggregate.getId(), zeroId, zeroStatus, 1)
		validateStatus(taskAggregate.getId(), firstId, firstStatus, 3)
		validateStatus(taskAggregate.getId(), secondId, secondStatus, 2)
	}

	private fun validateStatus(aggId: UUID, actualId: UUID, expectedStatus: TaskStatusEntity, expectedPosition: Int) {
		val status = taskController.getTaskStatus(aggId, actualId)
		Assertions.assertEquals(expectedStatus.color, status!!.color)
		Assertions.assertEquals(expectedStatus.name, status.name)
		Assertions.assertEquals(expectedPosition, status.position)
	}

	private fun createStatus(aggregateId: UUID, status: TaskStatusEntity) : TaskStatusCreatedEvent {
		return taskController.createTaskStatus(
			aggregateId,
			status.name,
			status.color.name,
			null
		)
	}

	private fun createUser(name: String) : UserCreatedEvent {
		return userController.createUser(
			"Login$name",
			name,
			"testPassword"
		)
	}

	private fun createProjectWithMember(ownerId: UUID, userId: UUID): ProjectAndMembersAggregateState? {
		val response = projectController.createProject(
			"testProject",
			ownerId
		)
		val newMember = projectController.createMember(
			response.projectId,
			userId
		)
		return projectController.getProject(response.projectId)
	}

	private companion object {
		val zeroStatus = TaskStatusEntity(
			name = "CREATED",
			projectId = UUID.randomUUID(),
			color = StatusColor.GREEN,
			position = 0,
		)
		val firstStatus = TaskStatusEntity(
			name = "1",
			projectId = UUID.randomUUID(),
			color = StatusColor.ORANGE,
			position = 0,
		)
		val secondStatus = TaskStatusEntity(
			name = "2",
			projectId = UUID.randomUUID(),
			color = StatusColor.BLUE,
			position = 0,
		)
		val thirdStatus = TaskStatusEntity(
			name = "3",
			projectId = UUID.randomUUID(),
			color = StatusColor.RED,
			position = 0,
		)
	}
}
