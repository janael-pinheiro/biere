package com.biere.catalog.infrastructure.adapter.input.rest.dtos

import jakarta.validation.constraints.NotEmpty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "DTO for user registration request")
data class UserRegistrationDTO(
    @field:NotEmpty(message = "The name of the user can not be empty")
    @Schema(description = "Full name of the user", example = "John Doe", minLength = 1, requiredMode = Schema.RequiredMode.REQUIRED)
    val name: String,

    @field:NotEmpty(message = "The e-mail of the user can not be empty")
    @Schema(description = "Email address of the user", example = "john.doe@example.com", minLength = 1, requiredMode = Schema.RequiredMode.REQUIRED)
    val email: String,

    @field:NotEmpty(message = "The password of the user can not be empty")
    @Schema(description = "Password for the user", example = "securePassword123", type = "string", format = "password", minLength = 1, requiredMode = Schema.RequiredMode.REQUIRED)
    val password: String
)