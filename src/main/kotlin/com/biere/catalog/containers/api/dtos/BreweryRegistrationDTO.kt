package com.biere.catalog.containers.api.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import org.jetbrains.annotations.NotNull

data class BreweryRegistrationDTO(
    @NotNull val name: String,
    @NotNull @JsonProperty("country_id") val countryId: Long)