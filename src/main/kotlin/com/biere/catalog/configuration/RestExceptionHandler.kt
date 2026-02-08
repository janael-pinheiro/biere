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
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import com.biere.catalog.containers.api.controllers.user.UserController
import com.biere.catalog.containers.api.controllers.beer.BeerController
import java.net.URI

import com.biere.catalog.containers.api.dtos.TokenRequestDTO
import java.time.OffsetDateTime
import java.util.UUID

@ControllerAdvice
class RestExceptionHandler : ResponseEntityExceptionHandler() {

    private val ERROR_TYPE_BASE = "https://biere.catalog.com/problem"

    private fun createProblemDetail(
        status: HttpStatus,
        title: String,
        detail: String?,
        path: String,
        errorType: String
    ): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(status, detail)
        problemDetail.title = title
        problemDetail.instance = URI.create(path)
        problemDetail.type = URI.create("$ERROR_TYPE_BASE/$errorType")
        
        // RFC 9457 Extensions
        problemDetail.setProperty("timestamp", OffsetDateTime.now())
        problemDetail.setProperty("trace-id", UUID.randomUUID().toString()) // Ideally should come from a tracing context
        
        return problemDetail
    }

    private fun buildResponse(problemDetail: ProblemDetail): ResponseEntity<Any> {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_PROBLEM_JSON
        return ResponseEntity(problemDetail, headers, HttpStatusCode.valueOf(problemDetail.status))
    }

    @ExceptionHandler(NotAuthorizedException::class)
    fun handleNotAuthorized(ex: NotAuthorizedException, request: HttpServletRequest): ResponseEntity<Any> {
        val problemDetail = createProblemDetail(HttpStatus.UNAUTHORIZED, "Unauthorized", ex.message, request.servletPath, "unauthorized")
        
        val authenticationLink = linkTo(methodOn(UserController::class.java).getToken(TokenRequestDTO("", ""))).toUri()
        problemDetail.setProperty("_links", mapOf(
            "authenticate" to mapOf("href" to authenticationLink.toString(), "method" to "POST")
        ))

        return buildResponse(problemDetail)
    }

    @ExceptionHandler(ConflictException::class)
    fun handleConflictException(ex: ConflictException, request: HttpServletRequest): ResponseEntity<Any> {
        val problemDetail = createProblemDetail(HttpStatus.CONFLICT, "Conflict", ex.message, request.servletPath, "conflict")
        return buildResponse(problemDetail)
    }

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(ex: NotFoundException, request: HttpServletRequest): ResponseEntity<Any> {
        val problemDetail = createProblemDetail(HttpStatus.NOT_FOUND, "Not Found", ex.message, request.servletPath, "not-found")
        return buildResponse(problemDetail)
    }

    @ExceptionHandler(ExpiredTokenException::class, InvalidTokenException::class)
    fun handleTokenExceptions(ex: Exception, request: HttpServletRequest): ResponseEntity<Any> {
        val problemDetail = createProblemDetail(HttpStatus.UNAUTHORIZED, "Unauthorized", ex.message, request.servletPath, "invalid-token")
        
        val authenticationLink = linkTo(methodOn(UserController::class.java).getToken(TokenRequestDTO("", ""))).toUri()
        problemDetail.setProperty("_links", mapOf(
            "authenticate" to mapOf("href" to authenticationLink.toString(), "method" to "POST")
        ))
        
        return buildResponse(problemDetail)
    }

    @ExceptionHandler(Exception::class)
    fun handleGlobalException(ex: Exception, request: HttpServletRequest): ResponseEntity<Any> {
        val problemDetail = createProblemDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Internal Server Error",
            "An unexpected error occurred. Please contact support with the trace-id.",
            request.servletPath,
            "internal-server-error"
        )
        // Log the actual exception here
        return buildResponse(problemDetail)
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val servletPath = (request as ServletWebRequest).request.servletPath
        val problemDetail = createProblemDetail(
            HttpStatus.UNPROCESSABLE_ENTITY,
            "Validation error",
            "One or more fields are invalid. Make the adjustment and try again.",
            servletPath,
            "validation-error"
        )

        val errors = ex.bindingResult.fieldErrors.map { fieldError ->
            mapOf(
                "field" to fieldError.field,
                "reason" to fieldError.defaultMessage
            )
        }
        problemDetail.setProperty("invalid-params", errors)

        val helpLink = linkTo(methodOn(BeerController::class.java).getBeersJson(null)).toUri()
        problemDetail.setProperty("_links", mapOf(
            "collection" to mapOf("href" to helpLink.toString(), "method" to "GET")
        ))

        return buildResponse(problemDetail)
    }
}
