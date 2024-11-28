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
	fun testDefaultStatusCreation() {
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
	}

	@Test
	fun testGetTaskStatus() {
		val owner = createUser("Owner")
		val user = createUser("Member")

		val project = createProjectWithMember(owner.userId, user.userId)

		val taskAggregate = taskController.getTaskStatusesAndTasks(project!!.getStatusesAndTasksAggregateId())
		val status = taskController.getTaskStatus(taskAggregate!!.getId(), taskAggregate.getStatuses()[0].id)
		Assertions.assertNotNull(status)
		Assertions.assertEquals("CREATED", status!!.name)
		Assertions.assertEquals(StatusColor.GREEN, status.color)
		Assertions.assertEquals(1, status.position)
	}

	@Test
	fun testNewStatusCreation() {
		val owner = createUser("Owner")
		val user = createUser("Member")

		val project = createProjectWithMember(owner.userId, user.userId)

		val newStatus = taskController.createTaskStatus(
			project!!.getStatusesAndTasksAggregateId(),
			"Completed",
			"ORANGE",
			null
		)
		val newStatusAgg = taskController.getTaskStatus(project.getStatusesAndTasksAggregateId(), newStatus.statusId)
		Assertions.assertNotNull(newStatusAgg)
		Assertions.assertEquals("Completed", newStatusAgg!!.name)
		Assertions.assertEquals(StatusColor.ORANGE, newStatusAgg.color)
		Assertions.assertEquals(2, newStatusAgg.position)
	}

	@Test
	fun testTaskCreation() {
		val owner = createUser("Owner")
		val user = createUser("Member")

		val project = createProjectWithMember(owner.userId, user.userId)

		val task = taskController.createTask(
			project!!.getStatusesAndTasksAggregateId(),
			project.getStatusesAndTasksAggregateId(),
			"Task",
			"Description1"
		)
		val taskAgg = taskController.getTask(project.getStatusesAndTasksAggregateId(), task.taskId)
		Assertions.assertNotNull(taskAgg)
		Assertions.assertEquals("Task", taskAgg!!.name)
		Assertions.assertEquals("Description1", taskAgg.description)
		Assertions.assertEquals(0, taskAgg.assignees.size)
		Assertions.assertEquals(project.getStatusesAndTasksAggregateId(), taskAgg.statusId)
	}

	@Test
	fun testTaskStatusChange() {
		val owner = createUser("Owner")
		val user = createUser("Member")

		val project = createProjectWithMember(owner.userId, user.userId)

		val newStatus = taskController.createTaskStatus(
			project!!.getStatusesAndTasksAggregateId(),
			"Completed",
			"ORANGE",
			null
		)

		val task = taskController.createTask(
			project.getStatusesAndTasksAggregateId(),
			newStatus.statusId,
			"Task",
			"Description1"
		)

		val taskAggregate = taskController.getTaskStatusesAndTasks(project.getStatusesAndTasksAggregateId())
		val defStatus = taskController.getTaskStatus(taskAggregate!!.getId(), taskAggregate.getStatuses()[0].id)
		val taskNewStatus = taskController.changeStatusForTask(
			project.getStatusesAndTasksAggregateId(),
			task.taskId,
			defStatus!!.id
		)
		val taskAggNewStatus = taskController.getTask(project.getStatusesAndTasksAggregateId(), taskNewStatus.taskId)
		Assertions.assertNotNull(taskAggNewStatus)
		Assertions.assertEquals("Task", taskAggNewStatus!!.name)
		Assertions.assertEquals(defStatus.id, taskAggNewStatus.statusId)
	}

	@Test
	fun testTaskNameAndDescriptionUpdate() {
		val owner = createUser("Owner")
		val user = createUser("Member")

		val project = createProjectWithMember(owner.userId, user.userId)

		val newStatus = taskController.createTaskStatus(
			project!!.getStatusesAndTasksAggregateId(),
			"Completed",
			"ORANGE",
			null
		)

		val task = taskController.createTask(
			project.getStatusesAndTasksAggregateId(),
			newStatus.statusId,
			"Task",
			"Description1"
		)

		val taskNewNameDescription = taskController.updateTask(
			project.getStatusesAndTasksAggregateId(),
			task.taskId,
			"newNameTask",
			"newDescription"
		)
		val taskAggNewNameDescription = taskController.getTask(project.getStatusesAndTasksAggregateId(), taskNewNameDescription.taskId)
		Assertions.assertNotNull(taskAggNewNameDescription)
		Assertions.assertEquals("newNameTask", taskAggNewNameDescription!!.name)
		Assertions.assertEquals("newDescription", taskAggNewNameDescription.description)
	}

	@Test
	fun testAddAssigneeToTask() {
		val owner = createUser("Owner")
		val user = createUser("Member")

		val project = createProjectWithMember(owner.userId, user.userId)

		val task = taskController.createTask(
			project!!.getStatusesAndTasksAggregateId(),
			project.getStatusesAndTasksAggregateId(),
			"Task",
			"Description1"
		)

		val taskWithAssignee = taskController.addAssigneeForTask(
			project.getStatusesAndTasksAggregateId(),
			task.taskId,
			user.userId
		)
		val taskAggWithAssignee = taskController.getTask(project.getStatusesAndTasksAggregateId(), taskWithAssignee!!.taskId)
		Assertions.assertNotNull(taskAggWithAssignee)
		Assertions.assertEquals(1, taskAggWithAssignee!!.assignees.size)
		Assertions.assertEquals(user.userId, taskAggWithAssignee.assignees[0])

		Assertions.assertThrows(IllegalArgumentException::class.java) {
			taskController.addAssigneeForTask(
				project.getStatusesAndTasksAggregateId(),
				task.taskId,
				user.userId
			)
		}
	}

	@Test
	fun testStatusesSwapPositions() {
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

		Assertions.assertThrows(IllegalArgumentException::class.java) {
			taskController.changeTaskStatusPosition(
				taskAggregate.getId(),
				secondId,
				10
			)
		}

		taskController.changeTaskStatusPosition(
			taskAggregate.getId(),
			thirdId,
			1
		)
		validateStatus(taskAggregate.getId(), zeroId, zeroStatus, 2)
		validateStatus(taskAggregate.getId(), firstId, firstStatus, 4)
		validateStatus(taskAggregate.getId(), secondId, secondStatus, 3)
		validateStatus(taskAggregate.getId(), thirdId, thirdStatus, 1)
	}

	@Test
	fun testStatusDeletion() {
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

		val taskAggregate = taskController.getTaskStatusesAndTasks(project.getStatusesAndTasksAggregateId())

		taskController.deleteTaskStatus(taskAggregate!!.getId(), firstId)
		validateStatus(taskAggregate.getId(), zeroId, zeroStatus, 1)
		validateStatus(taskAggregate.getId(), secondId, secondStatus, 2)

		taskController.createTask(
			taskAggregate.getId(),
			zeroId,
			"Task",
			"Description1"
		)

		Assertions.assertThrows(
			IllegalStateException::class.java
		) {
			taskController.deleteTaskStatus(taskAggregate.getId(), zeroId)
		}
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
		projectController.createMember(
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
