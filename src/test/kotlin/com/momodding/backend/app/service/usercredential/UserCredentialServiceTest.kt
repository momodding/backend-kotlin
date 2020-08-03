package com.momodding.backend.app.service.usercredential

import com.momodding.backend.app.dto.request.LoginRequest
import com.momodding.backend.app.dto.request.RegisterRequest
import com.momodding.backend.app.dto.response.AuthResponse
import com.momodding.backend.app.entity.UserCredential
import com.momodding.backend.app.entity.UserToken
import com.momodding.backend.app.repository.UserCredentialRepository
import com.momodding.backend.app.repository.UserTokenRepository
import com.momodding.backend.config.auth.JwtUtils
import com.momodding.backend.config.auth.TokenPayload
import com.momodding.backend.exception.AppException
import com.momodding.backend.exception.DataNotFoundException
import com.momodding.backend.exception.UnauthorizedException
import com.momodding.backend.utils.AppProperties
import com.momodding.backend.utils.JwtProperties
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Spy
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.context.annotation.Description
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.util.*

@ExtendWith(MockitoExtension::class)
class UserCredentialServiceTest {

	@Mock
	lateinit var userCredentialRepository: UserCredentialRepository

	@Mock
	lateinit var userTokenRepository: UserTokenRepository

	@Mock
	lateinit var jwtUtils: JwtUtils

	@Mock
	lateinit var appProperties: AppProperties

	@Spy
	@InjectMocks
	lateinit var userCredentialServiceImpl: UserCredentialServiceImpl

//	@Before
//	fun setUp() {
//		MockitoAnnotations.initMocks(this)
//		userCredentialServiceImpl = UserCredentialServiceImpl(userCredentialRepository, jwtUtils)
//	}

	private fun <T> any(): T {
		Mockito.any<T>()
		return uninitialized()
	}

	private fun <T> uninitialized(): T = null as T

	@BeforeEach
	fun setUp() {
		val jwtProperties = JwtProperties(
				expiration = 1,
				refresh = 1,
				signingKey = "abcd"
		)
		Mockito.lenient().`when`(appProperties.jwt).thenReturn(jwtProperties)
	}

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
		val tokenPayload = TokenPayload(
				ucId = user.id!!,
				email = user.email ?: "",
				userRole = user.role,
				issuedAt = Date().time
		)
		val userToken = UserToken(
				ucId = user.id,
				refreshToken = BCryptPasswordEncoder().encode(tokenPayload.toString()),
				expiredIn = Date(System.currentTimeMillis() + appProperties.jwt.refresh)
		)

		given(userCredentialRepository.findByEmail(request.username)).willReturn(Optional.of(user))
		given(userCredentialRepository.saveAndFlush(user)).willReturn(user)
		Mockito.`when`(userTokenRepository.saveAndFlush(Mockito.any(UserToken::class.java))).thenReturn(userToken)
		Mockito.`when`(jwtUtils.generateToken(this.any())).thenReturn("lorem.ipsum")
		val doLogin = userCredentialServiceImpl.doLogin(request)

		assertThat(doLogin, `is`(notNullValue()))
		assertThat((doLogin as AuthResponse).accessToken, `is`(notNullValue()))
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
		val tokenPayload = TokenPayload(
				ucId = 1,
				email = user.email ?: "",
				userRole = user.role,
				issuedAt = Date().time
		)
		val userToken = UserToken(
				ucId = 1,
				refreshToken = BCryptPasswordEncoder().encode(tokenPayload.toString()),
				expiredIn = Date(System.currentTimeMillis() + appProperties.jwt.refresh)
		)
		given(userCredentialRepository.findByEmail(request.email)).willReturn(null)
		given(userCredentialRepository.saveAndFlush(user)).willReturn(user)
		Mockito.`when`(userTokenRepository.saveAndFlush(Mockito.any(UserToken::class.java))).thenReturn(userToken)
		Mockito.`when`(jwtUtils.generateToken(this.any())).thenReturn("lorem.ipsum")

		val doRegister = userCredentialServiceImpl.doRegister(request)

		assertThat(doRegister, `is`(notNullValue()))
		assertThat((doRegister as AuthResponse).accessToken, `is`(notNullValue()))
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