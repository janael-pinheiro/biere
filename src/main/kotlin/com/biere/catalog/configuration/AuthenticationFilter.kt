package com.biere.catalog.configuration

import com.biere.catalog.core.exceptions.NotAuthorized
import com.biere.catalog.core.services.TokenService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class AuthenticationFilter(private val tokenService: TokenService, private val userDetailsService: UserDetailsService): OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ){
        val header: String? = request.getHeader("Authorization")
        if (header == null || !header.contains("Bearer ")){
            throw NotAuthorized("Token not provided")
        }
        val token: String = header.split("Bearer ")[1]
        if (!tokenService.isTokenValid(token)) {
            throw NotAuthorized("Invalid token")
        }
        val username = "janael"
        val userDetails = userDetailsService.loadUserByUsername(username)
        val authToken = UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.authorities
        )
        authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
        SecurityContextHolder.getContext().authentication = authToken
        filterChain.doFilter(request, response)
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        return request.servletPath in listOf("/v1/login", "/error")
    }
}