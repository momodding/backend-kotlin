package com.momodding.backend.app.controller.auth

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.momodding.backend.app.controller.v1.auth.AuthController
import com.momodding.backend.app.controller.v1.auth.AuthValidation
import com.momodding.backend.app.dto.request.LoginRequest
import com.momodding.backend.app.entity.UserCredential
import com.momodding.backend.app.service.usercredential.UserCredentialService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.ComponentScan
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@WebMvcTest(controllers = [AuthController::class], useDefaultFilters = false)
@ComponentScan("com.momodding.backend.app.controller.v1.auth")
class AuthControllerTest {

	private lateinit var mockMvc: MockMvc

	@Autowired
	private lateinit var webApplicationContext: WebApplicationContext

    @MockBean
	private lateinit var userCredentialService: UserCredentialService

    @MockBean
    private lateinit var authValidation: AuthValidation

	private var userList: List<UserCredential> = listOf(
		UserCredential(
			id = 1,
			username = "user1",
			password = "password1",
			email = "something1@mail.com",
			loginAttempt = 0,
			status = "Y",
			role = 1
		),
		UserCredential(
			id = 2,
			username = "user2",
			password = "password2",
			email = "something2@mail.com",
			loginAttempt = 0,
			status = "Y",
			role = 1
		),
		UserCredential(
			id = 3,
			username = "user3",
			password = "password3",
			email = "something3@mail.com",
			loginAttempt = 0,
			status = "Y",
			role = 1
		)
	)

	@BeforeEach
	fun setUp() {
		mockMvc = MockMvcBuilders
				.webAppContextSetup(webApplicationContext)
				.build()
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
		given(this.userCredentialService.doLogin(request)).willReturn(response)

		mockMvc.perform(post("/v1/auth/login")
				.header("X-Token-Auth", "ignore")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(jacksonObjectMapper().writeValueAsBytes(request)))
				.andExpect(status().isOk)
	}
}