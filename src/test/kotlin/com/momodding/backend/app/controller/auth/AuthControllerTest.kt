package com.momodding.backend.app.controller.auth

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.momodding.backend.app.controller.v1.auth.AuthController
import com.momodding.backend.app.dto.request.LoginRequest
import com.momodding.backend.app.entity.UserCredential
import com.momodding.backend.app.service.usercredential.UserCredentialService
import com.nhaarman.mockito_kotlin.given
import org.junit.Test
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [AuthController::class])
@ActiveProfiles("test")
class AuthControllerTest {

	@Autowired
	private lateinit var mockMvc: MockMvc

	@MockBean
	private lateinit var userCredentialService: UserCredentialService

	private lateinit var userList: mutableListOf<UserCredential>() = ArrayList()

	@BeforeEach
	fun setUp() {
		userList = mutableListOf<UserCredential>(
			UserCredential(
					id = 1,
					username = "user1",
					password = "password1",
					email = "something1@mail.com",
					loginAttempt = 0,
					status = "Y",
					role = 1),
			UserCredential(
					id = 2,
					username = "user2",
					password = "password2",
					email = "something2@mail.com",
					loginAttempt = 0,
					status = "Y",
					role = 1),
			UserCredential(
					id = 3,
					username = "user3",
					password = "password3",
					email = "something3@mail.com",
					loginAttempt = 0,
					status = "Y",
					role = 1)
		)
	}

	@Test
	fun shouldSuccess_whenLogin() {
		val request = LoginRequest(
				username = "user1",
				password = "password1"
		)
		val response = mutableMapOf<String, Any>()
		response["accessToken"] = "loremipsum"
		response["creds"] = userList[0]
		given(userCredentialService.doLogin(request)).willReturn(response)

		mockMvc.perform(post("v1/auth/login")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(jacksonObjectMapper().writeValueAsBytes(request)))
				.andExpect(status().isOk)
	}
}