package com.biere.catalog.configuration

import com.biere.catalog.containers.api.dtos.ApiError
import com.biere.catalog.core.exceptions.ConflictException
import com.biere.catalog.core.exceptions.ExpiredTokenException
import com.biere.catalog.core.exceptions.InvalidTokenException
import com.biere.catalog.core.exceptions.NotAuthorizedException
import com.biere.catalog.core.exceptions.NotFoundException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.net.URI

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
        val headers = HttpHeaders();
        val uriLocation: URI = URI.create("/login")
        headers.location = uriLocation
        return ResponseEntity(apiError, headers, HttpStatus.UNAUTHORIZED)
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

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(
        ex: NotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ApiError> {
        val apiError = ApiError(
            status = HttpStatus.NOT_FOUND.value(),
            error = HttpStatus.NOT_FOUND.reasonPhrase,
            message = ex.message,
            path = request.servletPath
        )
        return ResponseEntity(apiError, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(ExpiredTokenException::class)
    fun handleExpiredTokenException(
        ex: ExpiredTokenException,
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

    @ExceptionHandler(InvalidTokenException::class)
    fun handleInvalidTokenException(
        ex: InvalidTokenException,
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
