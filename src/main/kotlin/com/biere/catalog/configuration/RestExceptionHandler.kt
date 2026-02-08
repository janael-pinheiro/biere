package com.biere.catalog.configuration

import com.biere.catalog.core.exceptions.*
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.*
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.net.URI

@ControllerAdvice
class RestExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(NotAuthorizedException::class)
    fun handleNotAuthorized(
        ex: NotAuthorizedException,
        request: HttpServletRequest
    ): ResponseEntity<ProblemDetail> {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.message)
        problemDetail.title = "Unauthorized"
        problemDetail.instance = URI.create(request.servletPath)

        val headers = HttpHeaders()
        val uriLocation = URI.create("/login")
        headers.location = uriLocation
        
        return ResponseEntity(problemDetail, headers, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(ConflictException::class)
    fun handleConflictException(
        ex: ConflictException,
        request: HttpServletRequest
    ): ResponseEntity<ProblemDetail> {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.message)
        problemDetail.title = "Conflict"
        problemDetail.instance = URI.create(request.servletPath)
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail)
    }

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(
        ex: NotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ProblemDetail> {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.message)
        problemDetail.title = "Not Found"
        problemDetail.instance = URI.create(request.servletPath)
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail)
    }

    @ExceptionHandler(ExpiredTokenException::class, InvalidTokenException::class)
    fun handleTokenExceptions(
        ex: Exception,
        request: HttpServletRequest
    ): ResponseEntity<ProblemDetail> {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.message)
        problemDetail.title = "Unauthorized"
        problemDetail.instance = URI.create(request.servletPath)
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problemDetail)
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.UNPROCESSABLE_ENTITY,
            "One or more fields are invalid. Make the adjustment and try again."
        ).apply {
            title = "Validation error"
            type = URI.create("https://api.seuapp.com/errors/invalid-fields")
            instance = URI.create((request as ServletWebRequest).request.servletPath)

            val errors = ex.bindingResult.fieldErrors.map { fieldError ->
                mapOf(
                    "field" to fieldError.field,
                    "reason" to fieldError.defaultMessage
                )
            }

            setProperty("invalid-params", errors)
        }

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(problemDetail)
    }
}
