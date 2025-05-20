package com.sanalab.fanfare.auth.service

import com.fasterxml.jackson.annotation.JsonProperty
import com.sanalab.fanfare.auth.database.model.RefreshToken
import com.sanalab.fanfare.auth.database.model.User
import com.sanalab.fanfare.auth.database.repository.RefreshTokenRepository
import com.sanalab.fanfare.auth.database.repository.UserRepository
import com.sanalab.fanfare.core.component.HashEncoder
import com.sanalab.fanfare.core.util.ResponseHelper.responseException
import org.springframework.http.HttpStatus.*
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.MessageDigest
import java.time.Instant
import java.util.Base64

@Service
class AuthService(
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
    private val hashEncoder: HashEncoder,
    private val refreshTokenRepository: RefreshTokenRepository
) {
    data class TokenPair(
        @JsonProperty("access_token")
        val accessToken: String,
        @JsonProperty("refresh_token")
        val refreshToken: String
    )

    fun register(
        email: String,
        password: String,
        username: String,
        fullName: String? = null
    ): User {
        val user = userRepository.findByEmail(email.trim()) ?: userRepository.findByUsername(username.trim())
        if(user != null) throw responseException(CONFLICT, "User not found")

        return userRepository.save(
            User(
                email = email,
                hashedPassword = hashEncoder.encode(password),
                username = username,
                fullName = fullName
            )
        )
    }

    fun login(identifier: String, password: String): TokenPair {
        val user = userRepository.findByUsername(identifier)
            ?: userRepository.findByEmail(identifier)
            ?: throw BadCredentialsException("Invalid credentials.")

        if(!hashEncoder.matches(password, user.hashedPassword ?: ""))
            throw BadCredentialsException("Invalid credentials.")

        return storeAndGetToken(user.id)
    }

    @Transactional
    fun refresh(refreshToken: String): TokenPair {
        if(!jwtService.validateRefreshToken(refreshToken)) {
            throw responseException(UNAUTHORIZED,"Invalid refresh token.")
        }

        val userId = jwtService.getUserIdFromToken(refreshToken)
        val user = userRepository.findById(userId).orElseThrow {
            throw responseException(NOT_FOUND, "User not found.")
        }

        val hashed = hashToken(refreshToken)
        refreshTokenRepository.findByUserIdAndHashedToken(user.id, hashed)
            ?: throw responseException(UNAUTHORIZED, "Refresh token not recognized (maybe user or expired)")

        refreshTokenRepository.deleteByUserIdAndHashedToken(user.id, hashed)

        return storeAndGetToken(user.id, refreshToken)
    }

    private fun storeAndGetToken(userId: Long, oldRefreshToken: String? = null): TokenPair {
        val accessToken = jwtService.generateAccessToken(userId)
        val refreshToken = oldRefreshToken ?: jwtService.generateRefreshToken(userId)

        storeRefreshToken(userId, refreshToken)

        return TokenPair(accessToken, refreshToken)
    }

    private fun storeRefreshToken(userId: Long, refreshToken: String) {
        val hashed = hashToken(refreshToken)
        val expiryMs = jwtService.refreshTokenValidityMs
        val expiresAt = Instant.now().plusMillis(expiryMs)

        refreshTokenRepository.save(
            RefreshToken(
                userId = userId,
                expiresAt = expiresAt,
                hashedToken = hashed
            )
        )

    }

    private fun hashToken(token: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(token.encodeToByteArray())
        return Base64.getEncoder().encodeToString(hashBytes)
    }
}