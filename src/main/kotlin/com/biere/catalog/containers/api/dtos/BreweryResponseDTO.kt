package com.biere.catalog.containers.api.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class BreweryResponseDTO(
    val id: Long?,
    val name: String,
    @JsonProperty("country_name") val countryName: String)