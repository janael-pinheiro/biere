package com.biere.catalog.infrastructure.configuration

import com.biere.catalog.domain.exception.NotAuthorizedException
import com.biere.catalog.application.service.UserService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Profile
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.servlet.HandlerExceptionResolver

@Profile("dev")
@Component
class AuthenticationFilter(
    private val userService: UserService,
    private val userDetailsService: UserDetailsService,
    @Qualifier("handlerExceptionResolver") private val exceptionResolver: HandlerExceptionResolver): OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ){
        try {
            val header: String? = request.getHeader("Authorization")
            if (header == null || !header.contains("Bearer ")){
                throw NotAuthorizedException("Token not provided")
            }
            val token: String = header.split("Bearer ")[1]
            userService.isTokenValid(token)

            val username = userService.getEmailFromToken(token)
            val userDetails = userDetailsService.loadUserByUsername(username)
            val authToken = UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.authorities
            )
            authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
            SecurityContextHolder.getContext().authentication = authToken
            filterChain.doFilter(request, response)

        } catch (e: Exception){
            exceptionResolver.resolveException(request, response, null, e)
        }
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.servletPath
        return path == "/v1/users/login" ||
                path == "/v1/users/refresh-token" ||
                path == "/error" ||
                path.contains("/v3/api-docs") ||
                path.contains("/swagger-ui") ||
                path == "/actuator/health" ||
                path == "/actuator/prometheus" ||
                path == "/actuator/metrics"
    }
}