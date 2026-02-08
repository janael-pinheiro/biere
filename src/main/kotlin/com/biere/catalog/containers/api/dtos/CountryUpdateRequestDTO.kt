package com.biere.catalog.containers.api.dtos

import jakarta.validation.constraints.NotEmpty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "DTO for country update request")
data class CountryUpdateRequestDTO(
    @field:NotEmpty(message = "The name of the country can not be empty")
    @Schema(description = "New name of the country", example = "Netherlands", minLength = 1, requiredMode = Schema.RequiredMode.REQUIRED)
    val name: String
)
