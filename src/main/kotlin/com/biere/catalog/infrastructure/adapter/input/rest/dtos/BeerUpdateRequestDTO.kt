package com.biere.catalog.infrastructure.adapter.input.rest.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "DTO for beer update request (partial update)")
data class BeerUpdateRequestDTO(
    @Schema(description = "New name of the beer", example = "Heineken Silver", maxLength = 100)
    val name: String?,
    
    @JsonProperty("alcohol_content")
    @Schema(description = "New alcohol content percentage", example = "4.0", minimum = "0.0", maximum = "100.0")
    val alcoholContent: Float?,
    
    @JsonProperty("brewery_id")
    @Schema(description = "New ID of the brewery", example = "1")
    val breweryId: Long?,
    
    @JsonProperty("style_id")
    @Schema(description = "New ID of the beer style", example = "1")
    val styleId: Long?,
    
    @Schema(description = "New year of production", example = "2024")
    val year: Long?
)