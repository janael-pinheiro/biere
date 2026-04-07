package com.biere.catalog.infrastructure.configuration

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.UUID

@Component
class CorrelationIdFilter : OncePerRequestFilter() {

    companion object {
        const val CORRELATION_ID_HEADER = "X-Correlation-Id"
        const val CORRELATION_ID_LOG_VAR = "correlationId"
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val correlationId = request.getHeader(CORRELATION_ID_HEADER) ?: UUID.randomUUID().toString()
        
        try {
            // Put in MDC for logging
            MDC.put(CORRELATION_ID_LOG_VAR, correlationId)
            
            // Add to response header so the agent can correlate
            response.addHeader(CORRELATION_ID_HEADER, correlationId)
            response.addHeader("X-Request-Id", correlationId)
            
            filterChain.doFilter(request, response)
        } finally {
            MDC.remove(CORRELATION_ID_LOG_VAR)
        }
    }
}
