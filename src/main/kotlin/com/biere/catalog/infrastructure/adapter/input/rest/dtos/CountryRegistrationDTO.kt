package com.biere.catalog.infrastructure.adapter.input.rest.dtos

import jakarta.validation.constraints.NotEmpty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "DTO for country registration request")
data class CountryRegistrationDTO(
    @field:NotEmpty(message = "The name of the country can not be empty")
    @Schema(description = "Name of the country", example = "Netherlands", minLength = 1, requiredMode = Schema.RequiredMode.REQUIRED)
    val name: String
)