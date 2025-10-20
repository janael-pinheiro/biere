package com.biere.catalog.core.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class BreweryRegistrationDTO(val name: String, @JsonProperty("country_id") val countryId: Long)
