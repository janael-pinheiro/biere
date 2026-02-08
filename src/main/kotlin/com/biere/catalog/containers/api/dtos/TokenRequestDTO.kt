package com.biere.catalog.containers.api.dtos

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "DTO for authentication request")
data class TokenRequestDTO(
    @Schema(description = "User email address", example = "john.doe@example.com")
    val email: String,
    @Schema(description = "User password", example = "securePassword123", type = "string", format = "password")
    val password: String
)