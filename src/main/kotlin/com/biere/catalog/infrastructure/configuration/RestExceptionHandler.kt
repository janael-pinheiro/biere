package com.biere.catalog.infrastructure.configuration

import com.biere.catalog.domain.exception.*
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
import com.biere.catalog.infrastructure.adapter.input.rest.controllers.user.UserController
import com.biere.catalog.infrastructure.adapter.input.rest.controllers.beer.BeerController
import java.net.URI

import com.biere.catalog.infrastructure.adapter.input.rest.dtos.TokenRequestDTO
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
        errorType: String,
        remediation: String? = null
    ): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(status, detail)
        problemDetail.title = title
        problemDetail.instance = URI.create(path)
        problemDetail.type = URI.create("$ERROR_TYPE_BASE/$errorType")
        
        // RFC 9457 Extensions
        problemDetail.setProperty("timestamp", OffsetDateTime.now())
        problemDetail.setProperty("trace-id", UUID.randomUUID().toString())
        
        if (remediation != null) {
            problemDetail.setProperty("remediation", remediation)
        }
        
        return problemDetail
    }

    private fun buildResponse(problemDetail: ProblemDetail): ResponseEntity<Any> {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_PROBLEM_JSON
        return ResponseEntity(problemDetail, headers, HttpStatusCode.valueOf(problemDetail.status))
    }

    @ExceptionHandler(NotAuthorizedException::class)
    fun handleNotAuthorized(ex: NotAuthorizedException, request: HttpServletRequest): ResponseEntity<Any> {
        val problemDetail = createProblemDetail(
            HttpStatus.UNAUTHORIZED, 
            "Unauthorized", 
            ex.message, 
            request.servletPath, 
            "unauthorized",
            "Your credentials are missing or invalid. Use the 'authenticate' link to obtain a valid access token."
        )
        
        val authenticationLink = linkTo(methodOn(UserController::class.java).getToken(TokenRequestDTO("", ""))).toUri()
        problemDetail.setProperty("_links", mapOf(
            "authenticate" to mapOf("href" to authenticationLink.toString(), "method" to "POST", "title" to "Obtain authentication token")
        ))

        return buildResponse(problemDetail)
    }

    @ExceptionHandler(ConflictException::class)
    fun handleConflictException(ex: ConflictException, request: HttpServletRequest): ResponseEntity<Any> {
        val problemDetail = createProblemDetail(
            HttpStatus.CONFLICT, 
            "Conflict", 
            ex.message, 
            request.servletPath, 
            "conflict",
            ex.remediation
        )
        return buildResponse(problemDetail)
    }

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(ex: NotFoundException, request: HttpServletRequest): ResponseEntity<Any> {
        val problemDetail = createProblemDetail(
            HttpStatus.NOT_FOUND, 
            "Not Found", 
            ex.message, 
            request.servletPath, 
            "not-found",
            ex.remediation
        )
        
        val beerCollectionLink = linkTo(methodOn(BeerController::class.java).getBeersJson(null)).toUri()
        problemDetail.setProperty("_links", mapOf(
            "beer-collection" to mapOf("href" to beerCollectionLink.toString(), "method" to "GET", "title" to "List all beers")
        ))
        
        return buildResponse(problemDetail)
    }

    @ExceptionHandler(ExpiredTokenException::class, InvalidTokenException::class)
    fun handleTokenExceptions(ex: ExpiredTokenException, request: HttpServletRequest): ResponseEntity<Any> {
        val problemDetail = createProblemDetail(
            HttpStatus.UNAUTHORIZED, 
            "Unauthorized", 
            ex.message, 
            request.servletPath, 
            "invalid-token",
            ex.remediation
        )
        
        val authenticationLink = linkTo(methodOn(UserController::class.java).getToken(TokenRequestDTO("", ""))).toUri()
        val refreshLink = linkTo(methodOn(UserController::class.java).refreshToken(HttpHeaders())).toUri()
        
        problemDetail.setProperty("_links", mapOf(
            "authenticate" to mapOf("href" to authenticationLink.toString(), "method" to "POST", "title" to "Obtain new access token"),
            "refresh-token" to mapOf("href" to refreshLink.toString(), "method" to "POST", "title" to "Refresh access token")
        ))
        
        return buildResponse(problemDetail)
    }

    @ExceptionHandler(RateLimitException::class)
    fun handleRateLimitException(ex: RateLimitException, request: HttpServletRequest): ResponseEntity<Any> {
        val problemDetail = createProblemDetail(
            HttpStatus.TOO_MANY_REQUESTS,
            "Too Many Requests",
            ex.message,
            request.servletPath,
            "too-many-requests",
            "You have exceeded the allowed request rate. Slow down your requests and check the 'Retry-After' header for when you can try again."
        )
        return buildResponse(problemDetail)
    }

    @ExceptionHandler(IdempotencyKeyMissingException::class)
    fun handleIdempotencyKeyMissing(ex: IdempotencyKeyMissingException, request: HttpServletRequest): ResponseEntity<Any> {
        val problemDetail = createProblemDetail(
            HttpStatus.BAD_REQUEST,
            "Missing Idempotency Key",
            ex.message,
            request.servletPath,
            "idempotency-key-missing",
            ex.remediation
        )
        return buildResponse(problemDetail)
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException::class)
    fun handleAccessDeniedException(ex: org.springframework.security.access.AccessDeniedException, request: HttpServletRequest): ResponseEntity<Any> {
        val problemDetail = createProblemDetail(
            HttpStatus.FORBIDDEN,
            "Forbidden",
            ex.message,
            request.servletPath,
            "forbidden",
            "You do not have the required scope to perform this operation. Check your user permissions or contact an administrator to request the necessary access."
        )
        return buildResponse(problemDetail)
    }

    @ExceptionHandler(Exception::class)
    fun handleGlobalException(ex: Exception, request: HttpServletRequest): ResponseEntity<Any> {
        val problemDetail = createProblemDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Internal Server Error",
            "An unexpected error occurred. Please contact support with the trace-id.",
            request.servletPath,
            "internal-server-error",
            "A server-side error occurred. You can retry the request in a few moments or contact system administrators if it persists."
        )
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
            "One or more fields are invalid.",
            servletPath,
            "validation-error",
            "The data provided fails validation. Correct the fields listed in 'invalid-params' and retry. Refer to the OpenAPI schema for valid ranges and formats."
        )

        val errors = ex.bindingResult.fieldErrors.map { fieldError ->
            mapOf(
                "field" to fieldError.field,
                "reason" to fieldError.defaultMessage,
                "rejected-value" to fieldError.rejectedValue
            )
        }
        problemDetail.setProperty("invalid-params", errors)

        val docsLink = "/v3/api-docs" 
        problemDetail.setProperty("_links", mapOf(
            "documentation" to mapOf("href" to docsLink, "method" to "GET", "title" to "API Documentation")
        ))

        return buildResponse(problemDetail)
    }

}
