package com.biere.catalog.containers.api.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class BreweryRegistrationDTO(
    @field:NotEmpty(message = "The name of the brewery cannot be empty")
    @field:Size(max=100)
    val name: String,

    @JsonProperty("country_id")
    @field:NotNull(message = "CountryId can't be null")
    val countryId: Long?
)