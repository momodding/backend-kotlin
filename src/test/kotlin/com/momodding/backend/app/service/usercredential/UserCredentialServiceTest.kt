package com.momodding.backend.app.service.usercredential

import com.momodding.backend.app.dto.request.LoginRequest
import com.momodding.backend.app.dto.request.RegisterRequest
import com.momodding.backend.app.entity.UserCredential
import com.momodding.backend.app.repository.UserCredentialRepository
import com.momodding.backend.config.auth.JwtUtils
import com.momodding.backend.exception.AppException
import com.momodding.backend.exception.DataNotFoundException
import com.momodding.backend.exception.UnauthorizedException
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.context.annotation.Description
import java.util.*

@ExtendWith(MockitoExtension::class)
class UserCredentialServiceTest {

	@Mock
	lateinit var userCredentialRepository: UserCredentialRepository

	@Mock
	lateinit var jwtUtils: JwtUtils

	@InjectMocks
	lateinit var userCredentialServiceImpl: UserCredentialServiceImpl

//	@Before
//	fun setUp() {
//		MockitoAnnotations.initMocks(this)
//		userCredentialServiceImpl = UserCredentialServiceImpl(userCredentialRepository, jwtUtils)
//	}

	@Test
	@Description("login user using email and password")
	fun shouldSuccess_doLogin() {
		val user = UserCredential(
				id = 1,
				username = "user",
				email = "lol@mail.com",
				password = "password",
				loginAttempt = 0,
				status = "Y",
				role = 1
		)
		val request = LoginRequest(
				username = "lol@mail.com",
				password = "password"
		)
		given(userCredentialRepository.findByEmail(request.username)).willReturn(Optional.of(user))
		given(userCredentialRepository.saveAndFlush(user)).willReturn(user)
		val doLogin = userCredentialServiceImpl.doLogin(request)

		assertThat(doLogin, `is`(notNullValue()))
		assertThat((doLogin as Map<*, *>).filterKeys { it == "accessToken" }, `is`(notNullValue()))
	}

	@Test
	fun shouldFailed_doLoginWhenUsernameNotExist() {
		val request = LoginRequest(
				username = "username",
				password = "password1"
		)
		given(userCredentialRepository.findByEmail(request.username)).willReturn(null)

		assertThrows<DataNotFoundException> {
			userCredentialServiceImpl.doLogin(request)
		}
	}

	@Test
	fun shouldFailed_doLoginWhenUseWrongPassword() {
		val user = UserCredential(
				email = "user",
				password = "password",
				loginAttempt = 0,
				status = "Y",
				role = 1
		)
		val request = LoginRequest(
				username = "user",
				password = "password1"
		)
		given(userCredentialRepository.findByEmail(request.username)).willReturn(Optional.of(user))
		given(userCredentialRepository.saveAndFlush(user)).willReturn(user)

		assertThrows<UnauthorizedException> {
			userCredentialServiceImpl.doLogin(request)
		}
	}

	@Test
	fun shouldFailed_doLoginWhenStatusInactive() {
		val user = UserCredential(
				email = "user",
				password = "password",
				loginAttempt = 0,
				status = "N",
				role = 1
		)
		val request = LoginRequest(
				username = "user",
				password = "password"
		)
		given(userCredentialRepository.findByEmail(request.username)).willReturn(Optional.of(user))
		given(userCredentialRepository.saveAndFlush(user)).willReturn(user)

		assertThrows<UnauthorizedException> {
			userCredentialServiceImpl.doLogin(request)
		}
	}

	@Test
	fun shouldFailed_doLoginWhenLoginAttemptMoreThanThree() {
		val user = UserCredential(
				email = "user",
				password = "password",
				loginAttempt = 4,
				status = "Y",
				role = 1
		)
		val request = LoginRequest(
				username = "user",
				password = "password"
		)
		given(userCredentialRepository.findByEmail(request.username)).willReturn(Optional.of(user))
		given(userCredentialRepository.saveAndFlush(user)).willReturn(user)

		assertThrows<UnauthorizedException> {
			userCredentialServiceImpl.doLogin(request)
		}
	}

	@Test
	@Description("register user using email and password")
	fun shouldSuccess_doRegister() {
		val user = UserCredential(
				username = "user",
				password = "password",
				email = "something@mail.com",
				loginAttempt = 0,
				status = "Y",
				role = 1
		)
		val request = RegisterRequest(
				username = "user",
				password = "password",
				email = "something@mail.com",
				role = 1
		)
		given(userCredentialRepository.findByEmail(request.email)).willReturn(null)
		given(userCredentialRepository.saveAndFlush(user)).willReturn(user)

		val doRegister = userCredentialServiceImpl.doRegister(request)

		assertThat(doRegister, `is`(notNullValue()))
		assertThat((doRegister as Map<*, *>).filterKeys { it == "accessToken" }, `is`(notNullValue()))
	}

	@Test
	@Description("register user using email and password")
	fun shouldFailed_doRegisterWhenEmailAlreadyExist() {
		val user = UserCredential(
				username = "user",
				password = "password",
				email = "something@mail.com",
				loginAttempt = 0,
				status = "Y",
				role = 1
		)
		val request = RegisterRequest(
				username = "user",
				password = "password",
				email = "something@mail.com",
				role = 1
		)
		given(userCredentialRepository.findByEmail(request.email)).willReturn(Optional.of(user))

		assertThrows<AppException> {
			userCredentialServiceImpl.doRegister(request)
		}
	}

	@Test
	fun shouldSuccess_whenSaveOrUpdate() {
		val user = UserCredential(
				username = "user",
				password = "password",
				email = "something@mail.com",
				loginAttempt = 0,
				status = "Y",
				role = 1
		)
		given(userCredentialRepository.saveAndFlush(user)).willReturn(user)

		val save = userCredentialServiceImpl.saveOrUpdate(user)

		assertThat(save, `is`(notNullValue()))
	}

	@Test
	fun shouldFailed_whenSaveOrUpdate() {
		val user = UserCredential(
				username = "user",
				password = "password",
				email = "something@mail.com",
				loginAttempt = 0,
				status = "Y",
				role = 1
		)
		given(userCredentialRepository.saveAndFlush(user)).willReturn(UserCredential())

		val save = userCredentialServiceImpl.saveOrUpdate(user)

		assertThat(save, `is`(UserCredential()))
	}
}