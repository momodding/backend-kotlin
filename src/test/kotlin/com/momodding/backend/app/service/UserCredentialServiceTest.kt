package com.momodding.backend.app.service

import com.momodding.backend.app.dto.request.LoginRequest
import com.momodding.backend.app.entity.UserCredential
import com.momodding.backend.app.repository.UserCredentialRepository
import com.momodding.backend.app.service.usercredential.UserCredentialServiceImpl
import com.momodding.backend.config.auth.JwtUtils
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class UserCredentialServiceTest {

	@Mock
	lateinit var userCredentialRepository: UserCredentialRepository

	@Mock
	lateinit var jwtUtils: JwtUtils

	lateinit var userCredentialServiceImpl: UserCredentialServiceImpl

	@Before
	fun setUp() {
		MockitoAnnotations.initMocks(this)
		userCredentialServiceImpl = UserCredentialServiceImpl(userCredentialRepository, jwtUtils)
	}

	@Test
	fun shouldSuccessLogin() {
		val user = UserCredential(
				username = "user",
				password = "password",
				loginAttempt = 0,
				status = "Y",
				role = 1
		)
		val request = LoginRequest(
				username = "user",
				password = "password"
		)
		given(userCredentialRepository.findByEmail(request.username)).willReturn(Optional.of(user))
		given(userCredentialRepository.saveAndFlush(user)).willReturn(user)
		val doLogin = userCredentialServiceImpl.doLogin(request)

		assertThat(doLogin, `is`(notNullValue()))
	}
}