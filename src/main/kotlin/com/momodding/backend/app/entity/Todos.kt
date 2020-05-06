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
@Table(name = "user_todo")
data class Todos (
		@Id
		@Column(name = "ut_id")
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		val id: Long? = null,

		@Column(name = "ut_uc_id")
		val ucId: Long? = null,

		@Column(name = "ut_task_name")
		var name: String? = null,

		@Column(name = "ut_task_description")
		var description: String? = null,

		@JsonIgnore
		@Column(name = "ut_created_at")
		@CreationTimestamp
		val createdAt: Date? = null,

		@JsonIgnore
		@Column(name = "ut_updated_at")
		@UpdateTimestamp
		val updatedAt: Date? = null,

		@JsonIgnore
		@Column(name = "ut_deleted_at")
		var deletedAt: Date? = null
)