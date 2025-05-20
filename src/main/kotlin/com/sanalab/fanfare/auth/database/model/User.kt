package com.sanalab.fanfare.auth.database.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.Instant

@Entity(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    val email: String,

    @Column(nullable = false)
    val username: String,

    @Column(nullable = true, name = "full_name")
    val fullName: String? = null,

    @Column(nullable = true)
    val hashedPassword: String? = null,

    @Column(nullable = false, name = "created_at")
    val createdAt: Instant = Instant.now(),

    @Column(nullable = false, name = "updated_at")
    val  updatedAt: Instant = Instant.now()

)
