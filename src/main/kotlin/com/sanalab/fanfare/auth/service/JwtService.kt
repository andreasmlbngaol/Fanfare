package com.sanalab.fanfare.auth.service

import com.sanalab.fanfare.core.util.ResponseHelper.responseException
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Base64
import java.util.Date

@Service
class JwtService(
    @Value("\${jwt.secret}") private val jwtSecret: String
) {
    private val secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret))
    private val accessTokenValidityMs = 15 * 60 * 1000L // 15 minutes
    val refreshTokenValidityMs = 30 * 24 *60 * 60 * 1000L // 30 days

    private fun generateToken(
        userId: Long,
        type: String,
        expiry: Long
    ):  String {
        val now = Date()
        val expiryDate = Date(now.time + expiry)

        return Jwts.builder()
            .subject(userId.toString())
            .claim("type", type)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey, Jwts.SIG.HS256)
            .compact()
    }

    fun generateAccessToken(userId: Long): String {
        return generateToken(
            userId,
            "access",
            accessTokenValidityMs,
        )
    }

    fun generateRefreshToken(userId: Long): String {
        return generateToken(
            userId,
            "refresh",
            refreshTokenValidityMs,
        )
    }

    fun validateAccessToken(token: String): Boolean {
        return validateToken(token, "access")
    }

    fun validateRefreshToken(token: String): Boolean {
        return validateToken(token, "refresh")
    }

    private fun validateToken(token: String, type: String): Boolean {
        val claims = parseAllClaims(token) ?: return false
        val tokenType = claims["type"] as? String ?: return false
        return tokenType == type
    }

    // Authorization: Bearer <token>
    fun getUserIdFromToken(token: String): Long {
        val claims = parseAllClaims(token) ?: throw responseException(401, "Invalid token")
        return claims.subject.toLongOrNull() ?: 0L
    }

    private fun parseAllClaims(token: String): Claims? {
        val rawToken = if(token.startsWith("Bearer ")){
            token.removePrefix("Bearer ")
        } else token


        return try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(rawToken)
                .payload
        } catch (_: Exception) {
            null
        }
    }
}