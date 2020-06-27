package com.momodding.backend.app.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.CreationTimestamp
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "user_token")
data class UserToken(
        @Id
        @Column(name = "ut_id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null,

        @Column(name = "ut_uc_id")
        val ucId: Long? = null,

        @Column(name = "ut_token")
        val refreshToken: String? = null,

        @Column(name = "ut_expire_in")
        val expiredIn: Date? = null,

        @JsonIgnore
        @Column(name = "ut_created_at")
        @CreationTimestamp
        val createdAt: Date? = null

)