package com.biere.catalog.containers.api.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import org.jetbrains.annotations.NotNull

data class BeerRegistrationDTO(
    val name: String,
    @JsonProperty("country_id") @NotNull val countryId: Long,
    @JsonProperty("alcohol_content") @NotNull val alcoholContent: Float,
    @JsonProperty("brewery_id") @NotNull val breweryId: Long,
    @JsonProperty("style_id") @NotNull val styleId: Long)