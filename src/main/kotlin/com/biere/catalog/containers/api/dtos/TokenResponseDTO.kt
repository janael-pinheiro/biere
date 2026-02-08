package com.biere.catalog.containers.api.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "DTO for authentication response containing tokens")
data class TokenResponseDTO(
    @JsonProperty("access_token")
    @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1NiJ9...")
    val accessToken: String,
    @JsonProperty("refresh_token")
    @Schema(description = "JWT refresh token", example = "eyJhbGciOiJIUzI1NiJ9...")
    val refreshToken: String
)