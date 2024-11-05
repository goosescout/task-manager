package ru.quipy.projectAndMembers

import org.junit.jupiter.api.Assertions
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
	fun createProjectHappyPath() {
		val owner = createUser("Owner")
		val user = createUser("Member")
		val project = projectController.createProject(
			"testProject",
			owner.userId
		)
		Assertions.assertEquals(1, project.version)
		Assertions.assertEquals("testProject", project.projectName)

		val response = projectController.getProject(project.projectId)
		Assertions.assertNotNull(response)

		val ownerInProject = response!!.getMemberById(response.getMembers()[0].id)
		Assertions.assertNotNull(ownerInProject)
		Assertions.assertEquals("testProject", response.getName())
		Assertions.assertEquals("Owner", ownerInProject!!.name)
		Assertions.assertEquals("LoginOwner", ownerInProject.login)
		Assertions.assertEquals(response.getId(), ownerInProject.projectId)

		val newMember = projectController.createMember(
			response.getId(),
			user.userId
		)
		Assertions.assertNotNull(ownerInProject)
		Assertions.assertEquals("LoginMember", newMember.memberLogin)
		Assertions.assertEquals("Member", newMember.memberName)
		Assertions.assertEquals(user.userId, newMember.userId)
		Assertions.assertEquals(response.getId(), newMember.projectId)

		val memberResponse = projectController.getMember(
			response.getId(),
			newMember.memberId
		)
		Assertions.assertNotNull(memberResponse)
		Assertions.assertEquals("LoginMember", memberResponse!!.login)
		Assertions.assertEquals("Member", memberResponse.name)
		Assertions.assertEquals(user.userId, memberResponse.userId)
		Assertions.assertEquals(response.getId(), memberResponse.projectId)

		val memberNullResponse = projectController.getMember(
			response.getId(),
			UUID.randomUUID()
		)
		Assertions.assertNull(memberNullResponse)
	}

	private fun createUser(name: String) : UserCreatedEvent {
		return userController.createUser(
			"Login$name",
			name,
			"testPassword"
		)
	}
}
