package com.biere.catalog.infrastructure.adapter.input.rest.dtos

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "DTO for country response data")
data class CountryResponseDTO(
    @Schema(description = "Unique identifier of the country", example = "1")
    val id: Long?,
    @Schema(description = "Name of the country", example = "Netherlands")
    val name: String?
)