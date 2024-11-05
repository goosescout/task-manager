package ru.quipy.user

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.quipy.controller.UserController

@SpringBootTest
class UserTests {

	@Autowired
	private lateinit var userController: UserController

	@Test
	fun createUser() {
		val response = userController.createUser(
			"testLogin",
			"testName",
			"testPassword"
		)
		Assertions.assertEquals("testLogin", response.login)
		Assertions.assertEquals("testName", response.userName)
		Assertions.assertEquals("testPassword", response.password)
		Assertions.assertEquals(1, response.version)
	}

	@Test
	fun getUser() {
		userController.createUser(
			"testLogin1",
			"testName1",
			"testPassword1"
		)
		val responseForId = userController.createUser(
			"testLogin",
			"testName",
			"testPassword"
		)
		val response = userController.getUser(responseForId.userId)
		Assertions.assertNotNull(response)
		Assertions.assertEquals("testLogin", response!!.getLogin())
		Assertions.assertEquals("testName", response.getName())
		Assertions.assertEquals("testPassword", response.getPassword())
	}
}
