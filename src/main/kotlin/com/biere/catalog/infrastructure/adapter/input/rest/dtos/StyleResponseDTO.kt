package com.biere.catalog.infrastructure.adapter.input.rest.dtos

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "DTO for style response data")
data class StyleResponseDTO(
    @Schema(description = "Unique identifier of the style", example = "1")
    val id: Long,
    @Schema(description = "Name of the style", example = "IPA")
    val name: String
)
