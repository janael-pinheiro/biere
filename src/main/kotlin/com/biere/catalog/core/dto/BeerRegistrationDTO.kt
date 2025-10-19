package com.biere.catalog.core.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class BeerRegistrationDTO(val name: String, @JsonProperty("country_id") val countryId: Long, @JsonProperty("alcohol_content") val alcoholContent: Float, val brewery: String)
