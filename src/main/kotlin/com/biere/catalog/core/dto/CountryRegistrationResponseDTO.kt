package com.biere.catalog.core.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class CountryRegistrationResponseDTO(@JsonProperty("_links") val links: List<String>)
