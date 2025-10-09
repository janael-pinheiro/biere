package com.biere.catalog.configuration

import com.biere.catalog.core.dto.ApiError
import com.biere.catalog.core.exceptions.NotAuthorized
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class RestExceptionHandler {

    @ExceptionHandler(NotAuthorized::class)
    fun handleNotAuthorized(
        ex: NotAuthorized,
        request: HttpServletRequest
    ): ResponseEntity<ApiError> {
        val apiError = ApiError(
            status = HttpStatus.UNAUTHORIZED.value(),
            error = HttpStatus.UNAUTHORIZED.reasonPhrase,
            message = ex.message,
            path = request.servletPath
        )
        return ResponseEntity(apiError, HttpStatus.UNAUTHORIZED)
    }

}
