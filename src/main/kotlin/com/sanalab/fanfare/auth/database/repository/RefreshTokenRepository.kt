package com.sanalab.fanfare.auth.database.repository

import com.sanalab.fanfare.auth.database.model.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant

interface RefreshTokenRepository: JpaRepository<RefreshToken, Long> {
    fun deleteAllByExpiresAtBefore(now: Instant)
    fun findByUserIdAndHashedToken(userId: Long, hashedToken: String): RefreshToken?
    fun deleteByUserIdAndHashedToken(userId: Long, hashedToken: String)
}