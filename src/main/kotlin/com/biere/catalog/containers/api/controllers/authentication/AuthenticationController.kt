package com.biere.catalog.containers.api.controllers.authentication

import com.biere.catalog.core.services.UserService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/login")
class AuthenticationController(
    val userService: UserService) {
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getToken(@RequestBody loginRequest: TokenRequestDTO): ResponseEntity<TokenResponseDTO> {
        return ResponseEntity.ok(userService.generateToken(loginRequest));
    }
}