package com.momodding.backend.app.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "user_credential")
data class UserCredential (
		@Id
		@Column(name = "uc_id")
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		var id: Long? = null,

		@Column(name = "uc_username")
		val username: String? = null,

		@Column(name = "uc_email")
		val email: String? = null,

		@Column(name = "uc_password")
		val password: String? = null,

		@Column(name = "uc_status")
		var status: String? = null,

		@Column(name = "uc_role")
		val role: Long? = null,

		@Column(name = "uc_login_attempt")
		var loginAttempt: Long? = null,

		@JsonIgnore
		@Column(name = "uc_created_at")
		@CreationTimestamp
		val createdAt: Date? = null,

		@JsonIgnore
		@Column(name = "uc_updated_at")
		@UpdateTimestamp
		val updatedAt: Date? = null
)