package com.biere.catalog.infrastructure.configuration

import com.biere.catalog.domain.exception.IdempotencyKeyMissingException
import com.biere.catalog.domain.exception.RemediationMessage
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.servlet.HandlerExceptionResolver
import org.springframework.web.util.ContentCachingResponseWrapper
import java.util.Objects
import java.util.UUID

@Component
class IdempotencyFilter(
    @Value("\${idempotency.enabled:true}") private val enabled: Boolean,
    private val idempotencyRepository: IdempotencyRepository,
    @Qualifier("handlerExceptionResolver") private val exceptionResolver: HandlerExceptionResolver
) : OncePerRequestFilter() {

    private val safeHttpMethods = listOf(
        HttpMethod.GET.name(),
        HttpMethod.HEAD.name(),
        HttpMethod.OPTIONS.name(),
        HttpMethod.TRACE.name())

    companion object {
        const val IDEMPOTENCY_HEADER = "X-Idempotency-Key"
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (!enabled) {
            filterChain.doFilter(request, response)
            return
        }
        try {
            val idempotencyKey = request.getHeader(IDEMPOTENCY_HEADER)
            val method = request.method

            if (!isSafeMethod(method) && Objects.isNull(idempotencyKey)) {
                throw IdempotencyKeyMissingException(
                    "Missing $IDEMPOTENCY_HEADER header",
                    RemediationMessage.MISSING_IDEMPOTENCY_KEY.message
                )
            }

            if (Objects.isNull(idempotencyKey) || isSafeMethod(method)) {
                filterChain.doFilter(request, response)
                return
            }

            val cachedResponse = idempotencyRepository.get(idempotencyKey!!)
            if (cachedResponse != null) {
                replayResponse(response, cachedResponse)
                return
            }

            val responseWrapper = ContentCachingResponseWrapper(response)
            filterChain.doFilter(request, responseWrapper)
            saveResponse(idempotencyKey, responseWrapper)
            responseWrapper.copyBodyToResponse()
        } catch (e: Exception) {
            exceptionResolver.resolveException(request, response, null, e)
        }
    }

    private fun isSafeMethod(method: String): Boolean {
        return method in safeHttpMethods
    }

    private fun replayResponse(response: HttpServletResponse, cached: IdempotencyResponse) {
        response.status = cached.status
        response.contentType = cached.contentType
        cached.headers.forEach { (name, values) ->
            values.forEach { value -> response.addHeader(name, value) }
        }
        response.addHeader("X-Idempotency-Cache", "HIT")
        response.outputStream.write(cached.body)
        response.outputStream.flush()
    }

    private fun saveResponse(key: String, responseWrapper: ContentCachingResponseWrapper) {
        if (responseWrapper.status < 500) {
            val headers = mutableMapOf<String, List<String>>()
            responseWrapper.headerNames.forEach { name ->
                headers[name] = responseWrapper.getHeaders(name).toList()
            }

            val idempotencyResponse = IdempotencyResponse(
                status = responseWrapper.status,
                contentType = responseWrapper.contentType,
                body = responseWrapper.contentAsByteArray,
                headers = headers
            )
            idempotencyRepository.save(key, idempotencyResponse)
        }
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        return request.servletPath == "/v1/users/login"
    }
}
