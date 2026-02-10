package com.biere.catalog.infrastructure.adapter.input.rest.controllers.user

import com.biere.catalog.infrastructure.adapter.input.rest.dtos.TokenRequestDTO
import com.biere.catalog.infrastructure.adapter.input.rest.dtos.TokenResponseDTO
import com.biere.catalog.infrastructure.adapter.input.rest.dtos.UserRegistrationDTO
import com.biere.catalog.domain.model.InputUser
import com.biere.catalog.domain.port.input.UserUseCase
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.Parameter

@RestController
@RequestMapping("/v1/users")
@Tag(name = "Users", description = "User management APIs")
class UserController(
    val userService: UserUseCase) {
    @Operation(summary = "Login", description = "Authenticates a user and returns a token.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successfully authenticated"),
        ApiResponse(responseCode = "401", description = "Invalid credentials")
    ])
    @PostMapping("login", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getToken(@RequestBody loginRequest: TokenRequestDTO): ResponseEntity<TokenResponseDTO> {
        val token = userService.generateToken(loginRequest.email, loginRequest.password)
        return ResponseEntity.ok(TokenResponseDTO(accessToken = token.accessToken, refreshToken = token.refreshToken));
    }

    @Operation(summary = "Refresh Token", description = "Refreshes the authentication token.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successfully refreshed token"),
        ApiResponse(responseCode = "401", description = "Invalid or expired token")
    ])
    @PostMapping("refresh-token", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun refreshToken(@RequestHeader headers: HttpHeaders): ResponseEntity<TokenResponseDTO>{
        val token = userService.refreshToken(
            headers.get("authorization")?.get(0).toString().replace("Bearer", ""))
        return ResponseEntity.ok(TokenResponseDTO(accessToken = token.accessToken, refreshToken = token.refreshToken))
    }

    @Operation(summary = "Register a new user", description = "Registers a new user account.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "User created successfully"),
        ApiResponse(responseCode = "400", description = "Invalid input")
    ])
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createUser(@RequestBody user: UserRegistrationDTO): ResponseEntity<String> {
        userService.registerUser(InputUser(name = user.name, email = user.email, password = user.password))
        return ResponseEntity.created(URI("")).body("")
    }

    @Operation(summary = "Remove a user", description = "Removes a user by ID.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "User removed successfully"),
        ApiResponse(responseCode = "404", description = "User not found")
    ])
    @DeleteMapping("{userId}")
    fun removeUser(
        @Parameter(description = "ID of the user to be removed", example = "1")
        @PathVariable userId: Long
    ): ResponseEntity<String> {
        userService.removeUser(userId)
        return ResponseEntity.ok().body("")
    }
}