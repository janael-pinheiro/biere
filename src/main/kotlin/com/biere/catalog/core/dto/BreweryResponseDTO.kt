package com.biere.catalog.core.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class BreweryResponseDTO(val id: Long?, @JsonProperty("country_name") val countryName: String)
