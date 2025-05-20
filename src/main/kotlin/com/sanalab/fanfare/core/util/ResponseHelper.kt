package com.sanalab.fanfare.core.util

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.web.server.ResponseStatusException

object ResponseHelper {
    fun responseException(code: Int, message: String): ResponseStatusException {
        return ResponseStatusException(HttpStatusCode.valueOf(code), message)
    }

    fun responseException(code: HttpStatus, message: String): ResponseStatusException {
        return ResponseStatusException(code, message)
    }
}