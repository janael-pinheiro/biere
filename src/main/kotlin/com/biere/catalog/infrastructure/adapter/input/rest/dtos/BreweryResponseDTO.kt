package com.biere.catalog.infrastructure.adapter.input.rest.dtos

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "DTO for brewery response data")
data class BreweryResponseDTO(
    @Schema(description = "Unique identifier of the brewery", example = "1")
    val id: Long?,
    @Schema(description = "Name of the brewery", example = "Heineken Brewery")
    val name: String,
    @Schema(description = "Country of origin")
    val country: CountryResponseDTO)