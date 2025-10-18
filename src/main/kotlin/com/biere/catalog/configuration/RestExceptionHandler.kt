package com.biere.catalog.configuration

import com.biere.catalog.core.dto.ApiError
import com.biere.catalog.core.exceptions.ConflictException
import com.biere.catalog.core.exceptions.NotAuthorizedException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class RestExceptionHandler {

    @ExceptionHandler(NotAuthorizedException::class)
    fun handleNotAuthorized(
        ex: NotAuthorizedException,
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

    @ExceptionHandler(ConflictException::class)
    fun handleConflictException(
        ex: ConflictException,
        request: HttpServletRequest
    ): ResponseEntity<ApiError> {
        val apiError = ApiError(
            status = HttpStatus.CONFLICT.value(),
            error = HttpStatus.CONFLICT.reasonPhrase,
            message = ex.message,
            path = request.servletPath
        )
        return ResponseEntity(apiError, HttpStatus.CONFLICT)
    }

}
