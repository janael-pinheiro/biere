package com.biere.catalog.containers.controllers.authentication

import com.biere.catalog.core.services.TokenService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/login")
class AuthenticationController(
    @Autowired val tokenService: TokenService,
    private val authenticationManager: AuthenticationManager) {
    @PostMapping()
    fun getToken(@RequestBody loginRequest: LoginRequestDTO): ResponseEntity<String> {
        val authentication: Authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(loginRequest.username, loginRequest.password))
        return ResponseEntity.ok(tokenService.generateToken(authentication));
    }
}