package com.biere.catalog.containers.api.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class TokenResponseDTO(
    @JsonProperty("access_token") val accessToken: String,
    @JsonProperty("refresh_token") val refreshToken: String)