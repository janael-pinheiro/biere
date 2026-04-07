package com.biere.catalog.infrastructure.configuration

import com.biere.catalog.infrastructure.configuration.annotations.DeprecatedEndpoint
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor

@Component
class DeprecationInterceptor : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (handler is HandlerMethod) {
            val annotation = handler.getMethodAnnotation(DeprecatedEndpoint::class.java)
                ?: handler.beanType.getAnnotation(DeprecatedEndpoint::class.java)

            if (annotation != null) {
                response.addHeader("Deprecation", "true")
                if (annotation.sunset.isNotEmpty()) {
                    response.addHeader("Sunset", annotation.sunset)
                }
                if (annotation.successor.isNotEmpty()) {
                    response.addHeader("Link", "<${annotation.successor}>; rel=\"successor-version\"")
                }
            }
        }
        return true
    }
}
