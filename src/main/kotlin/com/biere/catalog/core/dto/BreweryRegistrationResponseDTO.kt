package com.biere.catalog.core.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class BreweryRegistrationResponseDTO(@JsonProperty("_links") val links: List<String>)
