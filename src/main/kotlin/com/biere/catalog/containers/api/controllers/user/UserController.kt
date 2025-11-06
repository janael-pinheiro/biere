package com.biere.catalog.containers.api.controllers.user

import com.biere.catalog.containers.api.dtos.TokenRequestDTO
import com.biere.catalog.containers.api.dtos.TokenResponseDTO
import com.biere.catalog.containers.api.dtos.UserRegistrationDTO
import com.biere.catalog.core.models.InputUser
import com.biere.catalog.core.services.UserService
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/v1/users")
class UserController(
    val userService: UserService) {
    @PostMapping("login", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getToken(@RequestBody loginRequest: TokenRequestDTO): ResponseEntity<TokenResponseDTO> {
        return ResponseEntity.ok(userService.generateToken(loginRequest));
    }

    @PostMapping("refresh-token", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun refreshToken(@RequestHeader headers: HttpHeaders): ResponseEntity<TokenResponseDTO>{
        return ResponseEntity.ok(userService.refreshToken(
            headers.get("authorization")?.get(0).toString().replace("Bearer", "")))
    }

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createUser(@RequestBody user: UserRegistrationDTO): ResponseEntity<String> {
        val userId = userService.registerUser(InputUser(name = user.name, email = user.email, password = user.password))
        return ResponseEntity.created(URI("")).body("")
    }

    @DeleteMapping("{userId}")
    fun removeUser(@PathVariable userId: Long): ResponseEntity<String> {
        userService.removeUser(userId)
        return ResponseEntity.ok().body("")
    }
}