package com.biere.catalog.containers.api.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull

data class BreweryUpdateRequestDTO(
    val name: String,
    @field:NotNull @JsonProperty("country_id") val countryId: Long)
