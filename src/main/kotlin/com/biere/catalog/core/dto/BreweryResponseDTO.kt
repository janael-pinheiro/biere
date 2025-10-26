package com.biere.catalog.core.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class BreweryResponseDTO(val id: Long?, val name: String, @JsonProperty("country_name") val countryName: String)
