package com.biere.catalog.containers.api.dtos

import jakarta.validation.constraints.NotEmpty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "DTO for style update request")
data class StyleUpdateRequestDTO(
    @field:NotEmpty(message = "The name of the style can not be empty")
    @Schema(description = "New name of the style", example = "Stout", minLength = 1, requiredMode = Schema.RequiredMode.REQUIRED)
    val name: String
)
