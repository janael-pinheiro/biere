package com.biere.catalog.containers.api.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "DTO for brewery update request")
data class BreweryUpdateRequestDTO(
    @Schema(description = "New name of the brewery", example = "Heineken International", minLength = 1, requiredMode = Schema.RequiredMode.REQUIRED)
    val name: String,
    
    @field:NotNull
    @JsonProperty("country_id")
    @Schema(description = "New ID of the country", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    val countryId: Long
)
