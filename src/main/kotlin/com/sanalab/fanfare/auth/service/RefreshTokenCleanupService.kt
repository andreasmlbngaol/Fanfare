package com.sanalab.fanfare.auth.service

import com.sanalab.fanfare.auth.database.repository.RefreshTokenRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class RefreshTokenCleanupService(
    private val repository: RefreshTokenRepository
) {
    @Scheduled(fixedRate = 60 * 60 * 1000)
    fun cleanupExpiredTokens() {
        val now  = Instant.now()
        repository.deleteAllByExpiresAtBefore(now)
    }
}