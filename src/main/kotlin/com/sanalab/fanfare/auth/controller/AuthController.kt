package com.sanalab.fanfare.auth.controller

import com.fasterxml.jackson.annotation.JsonProperty
import com.sanalab.fanfare.auth.service.AuthService
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService
) {
    data class RegisterRequest(
        @field:Email(message = "Invalid email format")
        val email: String,

        @field:Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
            message = "Password must be at least  8 characters long and contains at least 1 digit, uppercase, and lowercase character"
        )
        val password: String,

        @field:Pattern(
            regexp = "^(?=.{4,}$)[a-zA-Z0-9._]+(?<!\\.)$",
            message = "Username must be at least 4 characters, only letters, digits, underscores, and dots are allowed, and it cannot end with a dot"
        )
        val username: String,

        @JsonProperty("full_name")
        val fullName: String?
    )

    data class LoginRequest(
        val identifier: String,
        val password: String
    )

    data class RefreshRequest(
        @JsonProperty("refresh_token")
        val refreshToken: String
    )

    @PostMapping("/register")
    fun register(
        @Valid @RequestBody body: RegisterRequest
    ) {
        authService.register(
            body.email,
            body.password,
            body.username,
            body.fullName
        )
    }

    @PostMapping("/login")
    fun login(
        @RequestBody body: LoginRequest
    ): AuthService.TokenPair {
        return authService.login(body.identifier, body.password)
    }

    @PostMapping("/refresh")
    fun refresh(
        @RequestBody body: RefreshRequest
    ): AuthService.TokenPair {
        return authService.refresh(body.refreshToken)
    }
}