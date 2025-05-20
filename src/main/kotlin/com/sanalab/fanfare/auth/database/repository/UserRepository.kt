package com.sanalab.fanfare.auth.database.repository

import com.sanalab.fanfare.auth.database.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
    fun findByUsername(username: String): User?
}