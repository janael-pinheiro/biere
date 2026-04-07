package com.biere.catalog.infrastructure.configuration

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class VersioningInterceptor : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        // Semantic versioning links to help agents discover API evolution
        // rel="current" points to the base of the current major version
        response.addHeader("Link", "</v1>; rel=\"current\"")
        
        // We could also point to a documentation or a later version if it existed
        // response.addHeader("Link", "</v2>; rel=\"successor-version\"")
        
        return true
    }
}
