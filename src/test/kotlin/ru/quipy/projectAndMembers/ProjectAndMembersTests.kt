package ru.quipy.projectAndMembers

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.quipy.api.UserCreatedEvent
import ru.quipy.controller.ProjectAndMembersController
import ru.quipy.controller.UserController
import java.util.UUID

@SpringBootTest
class ProjectAndMembersTests {

	@Autowired
	private lateinit var userController: UserController

	@Autowired
	private lateinit var projectController: ProjectAndMembersController

	@Test
	fun testCreateProject() {
		val owner = createUser("Owner")
		val project = projectController.createProject(
			"testProject",
			owner.userId
		)
		Assertions.assertEquals(1, project.version)
		Assertions.assertEquals("testProject", project.projectName)
	}

	@Test
	fun testGetProject() {
		val owner = createUser("Owner")
		val project = projectController.createProject(
			"testProject",
			owner.userId
		)
		val response = projectController.getProject(project.projectId)
		Assertions.assertNotNull(response)
		Assertions.assertEquals("testProject", response!!.getName())
	}

	@Test
	fun testCreateMember() {
		val owner = createUser("Owner")
		val user = createUser("Member")
		val project = projectController.createProject(
			"testProject",
			owner.userId
		)
		val newMember = projectController.createMember(
			project.projectId,
			user.userId
		)
		Assertions.assertNotNull(newMember)
		Assertions.assertEquals("LoginMember", newMember.memberLogin)
		Assertions.assertEquals("Member", newMember.memberName)
		Assertions.assertEquals(user.userId, newMember.userId)
		Assertions.assertEquals(project.projectId, newMember.projectId)
	}

	@Test
	fun testCreateInvalidMember() {
		val owner = createUser("Owner")
		val project = projectController.createProject(
			"testProject",
			owner.userId
		)

		Assertions.assertThrows(Exception::class.java) {
			projectController.createMember(
				project.projectId,
				UUID.randomUUID()
			)
		}

		val response = projectController.getProject(project.projectId)
		Assertions.assertEquals(1, response!!.getMembers().size)
	}

	@Test
	fun testGetMember() {
		val owner = createUser("Owner")
		val user = createUser("Member")
		val project = projectController.createProject(
			"testProject",
			owner.userId
		)
		val newMember = projectController.createMember(
			project.projectId,
			user.userId
		)
		val memberResponse = projectController.getMember(
			project.projectId,
			newMember.memberId
		)
		Assertions.assertNotNull(memberResponse)
		Assertions.assertEquals("LoginMember", memberResponse!!.login)
		Assertions.assertEquals("Member", memberResponse.name)
		Assertions.assertEquals(user.userId, memberResponse.userId)
		Assertions.assertEquals(project.projectId, memberResponse.projectId)
	}

	@Test
	fun testGetNonExistentMember() {
		val owner = createUser("Owner")
		val project = projectController.createProject(
			"testProject",
			owner.userId
		)
		val memberNullResponse = projectController.getMember(
			project.projectId,
			UUID.randomUUID()
		)
		Assertions.assertNull(memberNullResponse)
	}

	@Test
	@Disabled
	fun testGetAllProjects() {
		val owner = createUser("Owner")

		val projects = projectController.getAllProjects()
		Assertions.assertEquals(0, projects.size)

		projectController.createProject(
			"testProject1",
			owner.userId
		)
		projectController.createProject(
			"testProject2",
			owner.userId
		)

		Thread.sleep(500)

		val newProjects = projectController.getAllProjects()
		println(newProjects)
		Assertions.assertEquals(2, newProjects.size)
		Assertions.assertEquals("testProject1", newProjects[0].name)
		Assertions.assertEquals("testProject2", newProjects[1].name)
	}

	@Test
	@Disabled
	fun testGetProjectWithAllStatuses() {
		val owner = createUser("Owner")
		val project = projectController.createProject(
			"testProject",
			owner.userId
		)

		Thread.sleep(500)

		val response = projectController.getProjectWithAllStatuses(project.projectId)
		Assertions.assertNotNull(response)
		Assertions.assertEquals("testProject", response.name)
		Assertions.assertEquals(1, response.statuses.size)
		Assertions.assertEquals("CREATED", response.statuses[0].name)
	}

	private fun createUser(name: String) : UserCreatedEvent {
		return userController.createUser(
			"Login$name",
			name,
			"testPassword"
		)
	}
}
