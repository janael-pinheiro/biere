package com.biere.catalog.containers.api.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class BreweryUpdateRequestDTO(
    val name: String,
    @JsonProperty("country_id") val countryId: Long)
