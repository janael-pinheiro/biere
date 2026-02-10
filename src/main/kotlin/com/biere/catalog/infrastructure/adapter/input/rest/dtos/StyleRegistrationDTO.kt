package com.biere.catalog.infrastructure.adapter.input.rest.dtos

import jakarta.validation.constraints.NotEmpty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "DTO for style registration request")
data class StyleRegistrationDTO(
    @field:NotEmpty(message = "The name of the style can not be empty")
    @Schema(description = "Name of the style", example = "IPA", minLength = 1, requiredMode = Schema.RequiredMode.REQUIRED)
    val name: String
)
