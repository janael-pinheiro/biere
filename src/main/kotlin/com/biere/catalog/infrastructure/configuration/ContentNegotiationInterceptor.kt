package com.biere.catalog.infrastructure.configuration

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class ContentNegotiationInterceptor : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        // AI agents should know that the response varies based on the Accept header
        // This is crucial for caching and for agents that explore multiple formats (like JSON vs CSV)
        response.addHeader("Vary", "Accept")

        // For PATCH requests, we should inform the agent which patch formats we support
        if (request.method == HttpMethod.PATCH.name()) {
            response.addHeader("Accept-Patch", "application/json")
        }

        return true
    }
}
